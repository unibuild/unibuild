package net.unibld.core;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.unibld.core.build.BuildException;
import net.unibld.core.build.BuildToolContext;
import net.unibld.core.build.ProjectLoader;
import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.task.TaskRegistry;
import net.unibld.core.util.PropertyPlaceHolderUtils;

/**
 * A class that instantiates a BuildProject either by creating a new one using a
 * ProjectConfig object or by loading one from the specified path.
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:16
 */
@Component
public class BuildProjectFactory {

	private static final Logger LOG = LoggerFactory.getLogger(BuildProjectFactory.class);

	@Autowired
	private ProjectLoader loader;

	
	@Value("${utility.goals}")
	private String utilityGoals;
	
	@Autowired
	private AbstractBeanFactory beanFactory;
	@Autowired
	private TaskRegistry taskRegistry;
	
	private Map<String,String> utilityGoalTasks;
	
	/**
	 * Initializes the factory component by loading utility goals from global config.
	 */
	@PostConstruct
	public void init() {
		LOG.info("Initializing utility goals...");
		utilityGoalTasks=new Hashtable<>();
		String[] split=utilityGoals.split(",");
		for (String goal:split) {
			String key=String.format("utility.goal.%s",goal.trim());
			String taskName=PropertyPlaceHolderUtils.getProperty(beanFactory, key);
			if (taskName==null) {
				throw new IllegalStateException("Task name not configured: "+key);
			}
			utilityGoalTasks.put(goal.trim(), taskName.trim());
			LOG.info("Utility goal {} inited with task: {}",goal,taskName);
		}
	}
	
	

	/**
	 * Loads a project previously saved to the path specified.
	 * @param context Build tool context
	 * @param projectFilePath Path of the project file to load 
	 * @param goal Goal selected to run
	 * @param args Command line arguments
	 * @return Project loaded
	 * @throws BuildException If any error occurs 
	 */
	public BuildProject loadProject(BuildToolContext context,String goal,String projectFilePath,String[] args) {
		ProjectConfig config = loader.loadProject(context,projectFilePath,args);
		
		return createProject(config,goal);
	}
	
	private String getDefaultGoal(BuildProject p) {
		List<BuildGoalConfig> goals = p.getProjectConfig().getGoalsConfig().getGoals();
		if (goals.isEmpty()) {
			throw new BuildException("No default goal could be found");
		}
		return goals.get(0).getName();
	}

	/**
	 * Creates a new project using the specified BuildConfig object, either loaded
	 * previously (copying) or just created.
	 * @param config Project configuration from project.xml 
	 * @param goal Goal name
	 * @return {@link BuildProject} instantiated
	 */
	public BuildProject createProject(ProjectConfig config,String goal){
		
		BuildProject p=new BuildProject();
		
		p.setName(config.getProjectName());
		p.setProjectConfig(config);
		
		
		
		if (StringUtils.isEmpty(goal)) {
			goal = getDefaultGoal(p);
		}
		p.setGoal(goal);
		
		if (utilityGoalTasks.containsKey(goal)) {
			String taskName = utilityGoalTasks.get(goal);
			LOG.info("Creating task {} for utility goal: {}...",taskName,goal);
		
			Class<? extends BuildTask> klazz=taskRegistry.getTaskClassByName(taskName);
			if (klazz==null) {
				throw new IllegalStateException("Task class not found: "+taskName);
			}
			
			try {
				BuildTask ret = klazz.newInstance();
				p.getTasks().add(ret);
			} catch (Exception ex) {
				LOG.error("Failed to instantiate utility task: "+taskName,ex);
				throw new IllegalStateException("Failed to instantiate utility task: "+taskName,ex);
			}
		} else {
			BuildGoalConfig gc=null;
			for (BuildGoalConfig g:config.getGoalsConfig().getGoals()) {
				if (g.getName().equals(goal)) {
					gc=g;
				}
			}
			
			if (gc==null) {
				throw new BuildException("Invalid goal: "+goal);
			}
			
			p.setTasks(gc.getTasks().getTasks());
			LOG.info("Goal '{}' configured with {} tasks",gc.getName(),gc.getTasks().getTasks().size());
		}
		
		return p;
	}
}//end BuildProjectFactory