package net.unibld.core.config.io;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.unibld.core.BuildTask;
import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.build.IncludeContext;
import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.BuildGoalsConfig;
import net.unibld.core.config.BuildTemplateConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.config.TaskConfigurations;
import net.unibld.core.config.TaskContext;
import net.unibld.core.persistence.model.ProjectAccessLevel;
import net.unibld.core.task.TaskRegistry;
import net.unibld.core.util.jaxb.JAXBUtil;
import net.unibld.core.var.VariableHelper;

/**
 * A class that loads a project configuration and uses DOM4J and JAXB at the same time for loading 
 * project tasks dynamically (from any dynamically defined tags).
 * @author andor
 *
 */
@Component
public class ProjectConfigurator extends Dom4JJaxbConfigurator<ProjectConfig> {
	private static final String PATH_TEMPLATE_TASKS = "//project/templates/template/tasks";

	private static final String PATH_GOALS = "//project/goals";

	private static final Logger LOG=LoggerFactory.getLogger(ProjectConfigurator.class);
	
	@Autowired
	private IProjectConfigLoadListener listener;
	@Autowired
	private TaskRegistry taskRegistry;
	
	private String[] externalArgs;
	
	@Override
	public ProjectConfig readConfig(IBuildContextAttributeContainer context,Class<ProjectConfig> klazz, InputStream is)
			throws Exception {
		clearDomPaths();
		addDomPath( PATH_TEMPLATE_TASKS );
		addDomPath( PATH_GOALS );
		
		String xml=IOUtils.toString(is,"UTF-8");
		ByteArrayInputStream bis=new ByteArrayInputStream(xml.getBytes("UTF-8"));
		
		ProjectConfig ret = (ProjectConfig) JAXBUtil.unmarshalFromStream(klazz, bis);
		jaxbInstanceLoaded(context,ret,externalArgs);
		Document document = DocumentHelper.parseText(xml);
		
		
        readDomContent(context,document, ret);
		return ret;
	}
	@Override
	protected void addDomPath(Document document, String path, int idx,
			ProjectConfig config) {
		if (path.startsWith(PATH_GOALS)) {
			Element goalsNode = (Element) document.selectSingleNode(PATH_GOALS);
			Element projNode = (Element) document.selectSingleNode("//project");
			if (goalsNode==null) {
				projNode.addElement("goals");
			}
			
			config.getGoalsConfig().setGoals(new ArrayList<BuildGoalConfig>());
			
			
			
			for (BuildGoalConfig cfg:config.getGoalsConfig().getGoals()) {
				try {
					addGoal(goalsNode,cfg);
					LOG.info("Goal added: {}",cfg.getClass().getName());
				} catch (IntrospectionException e) {
					LOG.error("Failed to add goal: "+cfg.getClass().getName(),e);
				}
			}

			
		}
	}
	private void addTask(Element tasksNode, BuildTask t) throws IntrospectionException {
		if (t==null) {
			throw new IllegalArgumentException("Task was null");
		}
		
		if (tasksNode==null) {
			throw new IllegalArgumentException("Tasks node was null");
		}
		
		Element taskNode=tasksNode.addElement(getTaskName(t));
		PropertyDescriptor[] propertyDescriptors = 
			    Introspector.getBeanInfo(t.getClass()).getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String val = getPropertyValue(t,propertyDescriptor);
			if (val!=null&&val.trim().length()>0) {
				taskNode.addAttribute(propertyDescriptor.getName(), val.trim());
				LOG.info("Attribute '{}' added with value: {}",propertyDescriptor.getName(),val);
			}
		}
		
	}
	
	private void addGoal(Element goalsNode, BuildGoalConfig g) throws IntrospectionException {
		if (g==null) {
			throw new IllegalArgumentException("Goal was null");
		}
		
		if (goalsNode==null) {
			throw new IllegalArgumentException("Goals node was null");
		}
		
		Element goalNode=goalsNode.addElement(getGoalName(g));
		PropertyDescriptor[] propertyDescriptors = 
			    Introspector.getBeanInfo(g.getClass()).getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (!propertyDescriptor.getName().equals("name")) {
				String val = getPropertyValue(g,propertyDescriptor);
				if (val!=null&&val.trim().length()>0) {
					goalNode.addAttribute(propertyDescriptor.getName(), val.trim());
					LOG.info("Attribute '{}' added with value: {}",propertyDescriptor.getName(),val);
				}
			}
		}
		
		List<BuildTask> tasks = g.getTasks().getTasks();
		if (tasks==null) {
			throw new IllegalArgumentException("Goal task list was null");
		}
		
		Element tasksNode = goalNode.addElement("tasks");
		
		for (BuildTask t:tasks) {
			addTask(tasksNode, t);
		}
		
	}
	private String getTaskName(BuildTask t) {
		if (t==null) {
			throw new IllegalArgumentException("Task was null");
		}
		return taskRegistry.getTaskNameByClass(t.getClass());
	}
	private String getGoalName(BuildGoalConfig t) {
		if (t==null) {
			throw new IllegalArgumentException("Goal was null");
		}
		return t.getName();
	}
	protected void jaxbInstanceLoaded(IBuildContextAttributeContainer container,ProjectConfig ret,String[] args) {
		if (ret!=null) {
			if (listener!=null) {
				listener.projectConfigLoaded(ret);
			}
	
			Map<String, String> userVars = VariableHelper.initVariables(ret.getVars(),args);
			container.setVariables(userVars);
		}
		
	}
	@SuppressWarnings("rawtypes")
	@Override
	protected void readDomPath(IBuildContextAttributeContainer context,Document document, String path, int idx,
			ProjectConfig config) {
		if (config==null) {
			throw new IllegalArgumentException("ProjectConfig was null");
		}
		if (document==null) {
			throw new IllegalArgumentException("DOM document was null");
		}
		if (path==null) {
			throw new IllegalArgumentException("DOM path was null");
		}

		if (config.getGoalsConfig()==null) {
			config.setGoalsConfig(new BuildGoalsConfig());
		}
		
		if (path.startsWith(PATH_GOALS)) {
			//goals
			Element goalsNode = (Element) document.selectSingleNode(path);
			readGoalsFromElement(context, config, goalsNode);
		} else if (path.startsWith(PATH_TEMPLATE_TASKS)) {
			//templates
			List templateTasks = document.selectNodes(path);
			readTemplateTasksFromElements(context, config, templateTasks);
		}
		
		
	}
	
	@SuppressWarnings("rawtypes")
	private void readTemplateTasksFromElements(IBuildContextAttributeContainer context, ProjectConfig config,
			List templateTasks) {
		for (Object o:templateTasks) {
			Element tasksElement=(Element) o;
			String templateName = tasksElement.getParent().attributeValue("name");
			BuildTemplateConfig template = findTemplate(config, templateName);
			
			LOG.info("Adding template: {}", templateName);
			for ( Iterator i = tasksElement.elementIterator(); i.hasNext(); ) {
		        Element taskElement = (Element) i.next();
		     
				BuildTask task = readTask(context, taskElement, false);
				
				if (template.getTasks()==null) {
					template.setTasks(new TaskConfigurations());
					template.getTasks().setTasks(new ArrayList<>());
				}
				template.getTasks().getTasks().add(task);
			}
			LOG.info("Added template {} with {} tasks", templateName, template.getTasks().getTasks().size());
			
		}
	}
	@SuppressWarnings("rawtypes")
	private void readGoalsFromElement(IBuildContextAttributeContainer context, ProjectConfig config,
			Element goalsNode) {
		if (goalsNode==null) {
			LOG.info("Goals node not found");
			return;
		}
		
		config.getGoalsConfig().setGoals(new ArrayList<BuildGoalConfig>());
		
		for ( Iterator i = goalsNode.elementIterator(); i.hasNext(); ) {
		    Element element = (Element) i.next();
		    if (element.getParent().getName().equals("goals")) {
		        BuildGoalConfig goal=readGoal(config, context,element);
		        if (goal!=null) {
		        	
		        	config.getGoalsConfig().getGoals().add(goal);
		        	LOG.info("Goal successfully read: {}",element.getName());
		        } 
		    }
		}
	}
	private BuildTemplateConfig findTemplate(ProjectConfig config, String templateName) {
		if (config.getTemplatesConfig()==null||config.getTemplatesConfig().getTemplates()==null ||
				config.getTemplatesConfig().getTemplates().isEmpty()) {
			throw new BuildException("No templates defined");
		}
		
		for (BuildTemplateConfig t:config.getTemplatesConfig().getTemplates()) {
			if (t.getName().equals(templateName)) {
				return t;
			}
		}
		throw new BuildException("Template not found: "+templateName);
	}
	@SuppressWarnings("rawtypes")
	private BuildGoalConfig readGoal(ProjectConfig config, IBuildContextAttributeContainer context,Element element) {
		if (element==null) {
			throw new IllegalArgumentException("DOM element was null");
		}
		
		try {
			BuildGoalConfig ret = parseGoalConfig(element);
			ret.setTasks(new TaskConfigurations());
			ret.getTasks().setTasks(new ArrayList<BuildTask>());
			
			Element tasks = element.element("tasks");
			if (tasks==null) {
				LOG.warn("<tasks/> element not found in: {}",element.getName());
				return ret;
			}
			for ( Iterator i = tasks.elementIterator(); i.hasNext(); ) {
		        Element taskElement = (Element) i.next();
		        
		        if (taskElement.getName().equals("include")) {
		        	List<BuildTask> includeTasks = readIncludeTasks(config, context, taskElement);
		        	 if (includeTasks!=null) {
				        	ret.getTasks().getTasks().addAll(includeTasks);
				        	LOG.info("Include tasks ({}) successfully read: {}",includeTasks.size(),element.getName());
				        } else {
				        	LOG.warn("Include tasks could not be read: {}",element.getName());
				        }
		        } else {
			        BuildTask task=readTask(context,taskElement,true);
			       	ret.getTasks().getTasks().add(task);
			       	LOG.info("Task successfully read: {} [{}]",element.getName(),task.getClass().getName());
			      
		        }
		    }
			return ret;
		} catch (Exception ex) {
			LOG.error(String.format("Failed to read goal: %s",element.getName()),ex);
			return null;
		}
	}
	private BuildGoalConfig parseGoalConfig(Element element) {
		BuildGoalConfig ret=new BuildGoalConfig();
		ret.setName(element.getName());
		
		String access = element.attributeValue("access");
		if (access!=null) {
			ret.setAccessLevel(ProjectAccessLevel.valueOf(access));
		}
		
		String confirm = element.attributeValue("confirm");
		if (confirm!=null) {
			ret.setConfirm(Boolean.parseBoolean(confirm));
		}
		return ret;
	}
	@SuppressWarnings("rawtypes")
	private List<BuildTask> readIncludeTasks(ProjectConfig config,IBuildContextAttributeContainer context, Element includeElement) {
		String templateName = includeElement.attributeValue("template");
		BuildTemplateConfig template = findTemplate(config, templateName);
		
		IncludeContext ictx=new IncludeContext(context);
		
		for ( Iterator i = includeElement.elementIterator(); i.hasNext(); ) {
            Element node = (Element) i.next();
            String varName=node.attributeValue("name");
            String varValue=node.attributeValue("value");
            LOG.info("Using {}={} for template: {}...",varName,varValue,templateName);
            ictx.setVariable(varName,varValue);
		}
		
		List<BuildTask> ret = new ArrayList<>();
		for (BuildTask task : template.getTasks().getTasks()) {
			try {
				PropertyDescriptor[] propertyDescriptors = 
					    Introspector.getBeanInfo(task.getClass()).getPropertyDescriptors();
				BuildTask t2 = task.getClone();
				if (task.getContext()!=null) {
					t2.setContext(task.getContext().getClone());
				}
				for (PropertyDescriptor propertyDescriptor:propertyDescriptors) {
					if (!propertyDescriptor.getPropertyType().equals(TaskContext.class)) {
						substitutePropertyValue(ictx, t2, propertyDescriptor);	
					}
				}
				ret.add(t2);
			} catch (Exception ex) {
				String msg = String.format("Failed to substitute template task: %s [%s]",task.getClass().getSimpleName(),templateName);
				LOG.error(msg,ex);
				throw new IllegalStateException(msg,ex);
			}
		}
		
		return ret;
	}
	@SuppressWarnings("rawtypes")
	private BuildTask readTask(IBuildContextAttributeContainer context,Element element,boolean substitute) {
		if (element==null) {
			throw new IllegalArgumentException("DOM element was null");
		}
		
		Class<? extends BuildTask> klazz=taskRegistry.getTaskClassByName(element.getName());
		if (klazz==null) {
			LOG.error("Task class not found: {}",element.getName());
			throw new IllegalStateException("Task class not found: "+element.getName());
		}
		
		try {
			BuildTask ret = klazz.newInstance();
			
			
			PropertyDescriptor[] propertyDescriptors = 
				    Introspector.getBeanInfo(ret.getClass()).getPropertyDescriptors();
			
			Map<String,PropertyDescriptor> descMap=new HashMap<>();
			if (propertyDescriptors!=null&&propertyDescriptors.length>0) {
				for (PropertyDescriptor pd:propertyDescriptors) {
					descMap.put(pd.getName(), pd);
				}
			}
			
			
			for ( Iterator i = element.attributeIterator(); i.hasNext(); ) {
	            Attribute attribute = (Attribute) i.next();
	            PropertyDescriptor propertyDescriptor = descMap.get(attribute.getName());
	            if (propertyDescriptor!=null) {
	            	setPropertyValue(context,ret, propertyDescriptor, attribute.getValue(),substitute);
	            } else {
	            	LOG.info("PropertyDescriptor not found: {}",attribute.getName());
	            }
	        }
			return ret;
		} catch (Exception ex) {
			String msg = String.format("Failed to read task: %s [%s]",element.getName(),klazz.getName());
			LOG.error(msg,ex);
			throw new IllegalStateException(msg,ex);
		}
	}
	@Override
	public void writeConfig(OutputStream out, ProjectConfig config)
			throws Exception {
		clearDomPaths();
		
		addDomPath( PATH_TEMPLATE_TASKS );
		addDomPath( PATH_GOALS );
		super.writeConfig(out, config);
	}
	public String[] getExternalArgs() {
		return externalArgs;
	}
	public void setExternalArgs(String[] externalArgs) {
		this.externalArgs = externalArgs;
	}

}
