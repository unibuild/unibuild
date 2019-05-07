package net.unibld.core.task;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.unibld.core.BuildTask;
import net.unibld.core.build.BuildConstants;
import net.unibld.core.build.BuildException;
import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.InstanceConfig;
import net.unibld.core.config.InstanceConfigurations;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.config.global.GlobalConfig;
import net.unibld.core.log.LoggingScheme;
import net.unibld.core.security.ICredentialStoreFactory;
import net.unibld.core.var.VariableSupport;

/**
 * An abstract base class for task runners: classes that implement the {@link ITaskRunner} interface.<br>
 * All task runners should be stateless so that they could be executed in a parallel manner.
 * @author andor
 *
 */
public abstract class BaseTaskRunner<T extends BuildTask> implements ITaskRunner<T>,ITaskLogger {
	public class TaskAttributeContainer implements IBuildContextAttributeContainer {
		private T task;
		
		public TaskAttributeContainer(T task) {
			this.task=task;
		}

		@Override
		public String getBuildContextAttribute(String name) {
			TaskContext ctx=task.getContext();
			if (ctx!=null) {
				String val = ctx.getAttribute(variableSupport,this,name);
				LoggerFactory.getLogger(getClass()).debug("Returning task context attribute: {} = {}...",name, val);
				return val;
			} 
			
			
			return null;
		}

		@Override
		public Map<String, String> getTaskContextAttributeMap() {
			Map<String,String> ret=new HashMap<String,String>();
			
			TaskContext ctx=task.getContext();
			Set<String> keys = ctx.getAttributeKeys();
			
			if (ctx!=null) {
				keys = ctx.getAttributeKeys();
				if (keys!=null) {
					for (String key: keys) {
						LoggerFactory.getLogger(getClass()).debug("Adding variable to map from task context: {} = {}...",key, ctx.getAttributeMap().get(key));
						ret.put(key, ctx.getAttributeMap().get(key));
					}
				}
			} 
			return ret;
		}

		/**
		 * @param userVars Map of user-defined variables
		 */
		public void setVariables(Map<String, String> userVars) {
			//not implemented
		}
		
	}
	
	protected Map<String,Object> attributesByBuildId=new Hashtable<>();
	@Autowired
	private LoggingScheme logging;
	@Autowired
	private GlobalConfig globalConfig;
	@Autowired
	protected VariableSupport variableSupport;
	
	@Autowired
	protected ICredentialStoreFactory credentialStoreFactory;
	
	/**
	 * Getter for the logging scheme, may be null
	 * @return The logging scheme or null
	 */
	public LoggingScheme getLogging() {
		return logging;
	}
	
	public void logDebug(String msg) {
		getLogger().debug(msg);
		if (getLogging()!=null){
			getLogging().logDebug(msg);
		}
	}
	public void logInfo(String msg) {
		getLogger().info(msg);
		if (getLogging()!=null){
			getLogging().logDebug(msg);
		}
	}
	public void logError(String msg) {
		getLogger().error(msg);
		if (getLogging()!=null){
			getLogging().logError(msg);
		}
	}
	public void logError(String msg,Exception ex) {
		getLogger().error(msg,ex);
		if (getLogging()!=null){
			getLogging().logError(msg,ex);
		}
	}
	
	public void logTaskErrorLogPath(String name,String path) {
		if (getLogging()!=null){
			getLogging().logTaskErrorLogPath(name,path);
		}
	}
	/*protected void logTaskResultOk(String msg) {
		if (logging!=null){
			logging.logTaskResult(getTaskName(), TaskResult.OK);
		}
		getLogger().debug(msg);
	}
	protected void logTaskResultFailed(String msg,Throwable ex) {
		getLogger().error(msg,ex);
		if (logging!=null){
			logging.logTaskResult(getTaskName(), TaskResult.FAILED);
			logging.logError(msg, ex);
		}	
	}*/
	public void logTask(String msg,String...args) {
		if (getLogging()!=null) {
			
			getLogging().logTask(getTaskName(), msg, args);
		}
	}
	protected abstract Logger getLogger();
	protected abstract String getTaskName();
	
	protected String getBuildDirPath(T task) {
		if (task==null) {
			throw new IllegalArgumentException("Task was null");
		}
		
		TaskContext ctx = task.getContext();
		if (ctx==null) {
			throw new IllegalStateException("TaskContext was null");
		}
		TaskAttributeContainer container=createAttributeContainer(task);
		return ctx.getAttribute(variableSupport,container,BuildConstants.VARIABLE_NAME_BUILD_DIR);
		
	}
	
	protected GlobalConfig getGlobalConfig() {
		return globalConfig;
	}
	
	protected ProjectConfig getProjectConfig(T task) {
		if (task==null) {
			throw new IllegalArgumentException("Task was null");
		}
		
		TaskContext ctx = task.getContext();
		if (ctx==null) {
			throw new IllegalStateException("TaskContext was null");
		}
		
		return ctx.getProjectConfig();
		
	}
	
	
	protected BuildGoalConfig getGoalConfig(BuildTask task) {
		if (task==null) {
			throw new IllegalArgumentException("Task was null");
		}
		
		TaskContext ctx = task.getContext();
		if (ctx==null) {
			throw new IllegalStateException("TaskContext was null");
		}
		return ctx.getGoalConfig();
		
	}
	public String getBuildContextAttribute(T task,String name) {
		TaskAttributeContainer container=createAttributeContainer(task);
		return container.getBuildContextAttribute(name);
	}

	public BaseTaskRunner<T>.TaskAttributeContainer createAttributeContainer(
			T task) {
		return new TaskAttributeContainer(task);
	}
	
	public void setTaskContextAttribute(BuildTask task,String name,String value) {
		TaskContext ctx=task.getContext();
		if (ctx!=null) {
			ctx.addAttribute(name, value);
			LoggerFactory.getLogger(getClass()).info("Task context attribute set: {} = {}...",name, value);
		} else {
			LoggerFactory.getLogger(getClass()).warn("TaskContext not found");
		}
		
		
		
	}
	
	public Map<String,String> getTaskContextAttributeMap(T task) {
		TaskAttributeContainer container=createAttributeContainer(task);
		return container.getTaskContextAttributeMap();
		
	}
	
	protected InstanceConfig getInstanceConfigByName(T task,String instanceName) {
		InstanceConfigurations instances = getProjectConfig(task).getInstances();
		if (instances==null||instances.getInstanceConfigs()==null) {
			throw new IllegalStateException("No instance configurations specified");
		}
		for (InstanceConfig ic:instances.getInstanceConfigs()) {
			if (ic.getNodeName().equals(instanceName)) {
				return ic;
			}
		}
		throw new IllegalArgumentException("Instance not found: "+instanceName);
	}
	
	@Override
	public void run(T task) {
		if (task==null) {
			throw new IllegalArgumentException("Task to run was null");
		}
		logDebug(String.format("%s started",getTaskName()));

		if (task.isExecuting()) {
			ExecutionResult result=execute(task);
			
			onComplete();
			if (!result.isSuccess()) {
				String msg = String.format("%s failed: %s",getTaskName(), result.getMessage());
				throw new BuildException(msg,result.getException(),result.getLogFilePath());
			}
		} else {
			LoggerFactory.getLogger(getClass()).info("Skipping task {} as its executeCondition property evaluated to: {}...",task.getClass().getSimpleName(),task.getExecuteCondition());
			logTask(String.format("Skipping task: %s...",getTaskName()));
		}
	}
	
	protected void onComplete() {
		// does nothing
		
	}
	protected abstract ExecutionResult execute(T task);
	
	
}
