package net.unibld.core.build;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import net.unibld.core.BuildProject;
import net.unibld.core.BuildTask;
import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.config.Variable;
import net.unibld.core.config.Variables;
import net.unibld.core.log.LoggerHelper;
import net.unibld.core.log.LoggingScheme;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.BuildTaskResult;
import net.unibld.core.service.BuildService;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ITaskRunner;
import net.unibld.core.task.TaskRegistry;
import net.unibld.core.task.annotations.Task;

/**
 * A Spring bean that runs a build on a specified project.
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:30
 */
@Component
public class ProjectBuilder {

	private static final Logger LOG=LoggerFactory.getLogger(ProjectBuilder.class);

	@Autowired
	private LoggingScheme loggingScheme;
	
	

	@Autowired
	private BuildService buildService;
	
	
	@Autowired
	private BuildToolContextHolder contextHolder;
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private TaskRegistry taskRegistry;
	
	
	
	/**
	 * Runs the selected goal using the specified parameters
	 * @param req Build request containing all necessary parameters
	 * 
	 * @throws BuildException If an error occurs
	 */
	public void run(BuildRequest req) {
		if (req==null) {
			throw new IllegalArgumentException("Build request was null");
		}
		if (req.getProject()==null) {
			throw new BuildException("Project to build was null");
		}
		if (req.getGoal()==null) {
			throw new BuildException("Goal to run was null");
		}
		
		if (req.getProject().getTasks()==null||req.getProject().getTasks().isEmpty()) {
			throw new BuildException(BuildConstants.ERR_NO_TASKS);
		}
		
		if (req.getGoal().equals(BuildConstants.CLEAR_PASSWORDS_GOAL)) {
			LOG.info("Clearing all passwords...");
		} else {
			LOG.info("Running goal '{}' on project {} started...",req.getGoal(),req.getProject().getName());
		}
		
		boolean verbose=req.getVerbosity()!=null && req.getVerbosity().isVerbose();
		boolean trace=req.getVerbosity()!=null && req.getVerbosity().isTrace();
		loggingScheme.addLoggers(LoggerHelper.createLoggers(req.getProject().getProjectConfig().getLogConfig(),
				verbose, trace));
		
		welcome(req.getProject(),req.getGoal());
		
		BuildToolContext context = contextHolder.getContext(req.getBuildId());
		
		LOG.debug("Checking build dir: {}...",context.getBuildDir());
		File buildDir=new File(context.getBuildDir());
		if (!buildDir.exists()) {
			LOG.info("Creating build dir on the fly: {}...",context.getBuildDir());
			buildDir.mkdirs();
		}
		
		
		validateVars(req.getProject().getProjectConfig().getVars());
		
		List<BuildTask> tasks = req.getProject().getTasks();
		LOG.info("Validating {} tasks...",tasks.size());
		
		List<ITaskRunner<? extends BuildTask>> runners= createRunners(tasks);
		LOG.info("Validation of {} tasks succeeded",tasks.size());
		
		
		Build b = buildService.startBuild(req.getBuildId(),req.getProject(), new File(context.getProjectFile()).getAbsolutePath(), req.getGoal(), req.getUserId());
		List<BuildEventListener> listenerList=req.getBuildEventListeners();
		if (listenerList!=null) {
			for (BuildEventListener l:listenerList) {
				l.buildStarted(b);
			}
		}
		
		List<ProgressListener> plistenerList=req.getProgressListeners();
		
		LOG.info("Started executing {} tasks...",tasks.size());
		
		BuildTaskResult currentTaskResult=null;
		
		for (int i=1;i<=runners.size();i++) {
			
			BuildTask t = tasks.get(i-1);
			BaseTaskRunner<? extends BuildTask> runner = (BaseTaskRunner<? extends BuildTask>) runners.get(i-1);
			String taskName = taskRegistry.getTaskNameByClass(t.getClass());
			try {
				if (buildService.isBuildCancelled(b.getId())) {
					throw new BuildException("Build cancelled by user.");
				}
				currentTaskResult=buildService.taskStarted(req.getBuildId(), t.getClass().getName(), taskName, i);
				
				executeTask(context,req.getProject(),req.getGoal(),i,t,runner);
				loggingScheme.logTaskResult(t.getClass().getName(), TaskResult.OK);
				
				int perc=Math.round(100*((float)i)/((float)runners.size()));
				
				if (plistenerList!=null) {
					for (ProgressListener l:plistenerList) {
						l.progress(req.getBuildId(),perc,i);
					}
				}
				
				buildService.taskCompleted(currentTaskResult.getId());
				
			} catch (BuildException bex) {
				handleException(req, t, i, currentTaskResult, bex);
				throw bex;
			} catch (Exception ex ) {
				handleException(req, t, i, currentTaskResult, ex);
				throw new BuildException("Failed to execute "+t.getClass().getSimpleName(), ex);
			}
		}
		
		
		
		LOG.info("Completed executing {} tasks...",tasks.size());
		buildService.buildCompleted(req.getBuildId());
		
		if (listenerList!=null) {
			for (BuildEventListener l:listenerList) {
				l.buildCompleted(req.getBuildId());
			}
		}
		
		if (plistenerList!=null) {
			for (ProgressListener l:plistenerList) {
				l.progress(req.getBuildId(),100,runners.size());
			}
		}
	}

	private String handleException(BuildRequest req, BuildTask t, int i,BuildTaskResult currentTaskResult, Exception ex) {
		String klazzName = t.getClass().getSimpleName();
		loggingScheme.logTaskResult(klazzName, TaskResult.FAILED);
		loggingScheme.logTaskFailureReason(klazzName, getFailureReason(t,ex));
		
		String logFilePath=null;
		if (ex instanceof BuildException) {
			BuildException be=(BuildException) ex;
			if (be.getLogFilePath()!=null) {
				File lf=new File(be.getLogFilePath());
				if (lf.exists()&&lf.isFile()&&lf.length()>0) {
					loggingScheme.logTaskErrorLogPath(klazzName, be.getLogFilePath());
					logFilePath=be.getLogFilePath();
					
					if (req.getBuildEventListeners()!=null) {
						for (BuildEventListener l:req.getBuildEventListeners()) {
							l.errorLogCreated(req.getBuildId(),klazzName,be.getLogFilePath());
						}
					}
				}
			}
		

			
		}


		buildService.buildFailed(req.getBuildId(), currentTaskResult!=null?currentTaskResult.getId():null, klazzName,i, ex,logFilePath);
		
		if (req.getBuildEventListeners()!=null) {
			for (BuildEventListener l:req.getBuildEventListeners()) {
				l.buildFailed(req.getBuildId(),klazzName,i,ex);
			}
		}
	
		return klazzName;
	}

	private void validateVars(Variables vars) {
		if (vars==null||vars.getVariables()==null||vars.getVariables().isEmpty()) {
			LOG.info("No variables to validate");
			return;
		}
		
		for (Variable v:vars.getVariables()) {
			if (!isValidVariable(v)) {
				throw new BuildException("Illegal variable: "+v.getName());
			}
		}
	}


	private boolean isValidVariable(Variable v) {
		if (v.getName()==null||v.getName().trim().length()==0) {
			return false;
		}
		if (v.getName().contains("=")) {
			return false;
		}
		if (v.getName().contains("$")) {
			return false;
		}
		if (v.getName().contains("#")) {
			return false;
		}
		if (v.getName().contains("-")) {
			return false;
		}
		if (v.getName().contains("+")) {
			return false;
		}
		if (v.getName().contains("!")) {
			return false;
		}
		return !(v.getName().contains(" "));
		
	}


	private String getFailureReason(BuildTask task,Exception ex) {
		if (task!=null && task.getContext()!=null) {
			String ret = task.getContext().getFailureReason();
			if (ret!=null) {
				return ret;
			}
		}
		if (ex!=null&&ex.getCause()!=null&&ex.getCause().getMessage()!=null) {
			return ex.getCause().getMessage();
		}
		if (ex!=null&&ex.getMessage()!=null) {
			return ex.getMessage();
		}
		return "Unknown error";
	}


	


	private void welcome(BuildProject project,String goal) {
		String logFile=null;
		if (loggingScheme.containsFileLogging()) {
			logFile=loggingScheme.getLogFile();
		}
		loggingScheme.welcome(project, goal, logFile!=null? new File(logFile).getAbsolutePath():null);
		
	}

	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void executeTask(BuildToolContext context,BuildProject project,
			String goal,int taskNumber, BuildTask buildTask, BaseTaskRunner runner) {
		if (buildTask==null) {
			throw new IllegalArgumentException("Build task was null");
		}
		if (runner==null) {
			throw new IllegalArgumentException("Runner was null");
		}
		
		
		String taskName = taskRegistry.getTaskNameByClass(buildTask.getClass());
		if (taskName==null) {
			throw new IllegalStateException("Task name not found for: "+buildTask.getClass().getName());
		}
		
		LOG.info("[{}] {} started -----------------------",taskNumber,taskName);
		LOG.debug("Using runner: {}",runner.getClass().getName());

		prepareRunner(context,project,goal,taskNumber,buildTask,runner);
		runner.run(buildTask);
		
		LOG.info("[{}] {} completed -----------------------",taskNumber,taskName);
		
	}

	private TaskContext prepareRunner(BuildToolContext context,BuildProject project,
			String goal,int taskNumber, BuildTask buildTask, ITaskRunner<? extends BuildTask> runner) {
		if (buildTask==null) {
			throw new IllegalArgumentException("Build task was null");
		}
		if (runner==null) {
			throw new IllegalArgumentException("Runner was null");
		}
		

		TaskContext taskCtx = buildTask.getContext();
		if (taskCtx==null) {
			LOG.debug("Creating TaskContext as it was null previously...");
			taskCtx=new TaskContext();
			buildTask.setContext(taskCtx);
		}
		
		taskCtx.setBuildId(context.getId());
		taskCtx.setTaskIndex(taskNumber);
		
		taskCtx.addAttribute(BuildConstants.VARIABLE_NAME_PROJECT_DIR, context.getProjectDir());
		LOG.info("Using project dir: {}...",context.getProjectDir());
		taskCtx.addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, context.getBuildDir());
		LOG.info("Using project dir: {}...",context.getBuildDir());
		
		
		taskCtx.setProjectConfig(project.getProjectConfig());
		
		//adding variables
		if (context.getVariables()!=null) {
			for (String key:context.getVariables().keySet()) {
				LOG.info("Adding global variable to task context: {}",key);
				taskCtx.addAttribute(key, context.getVariables().get(key));
			}
		}
		if (project.getProjectConfig().getGoalsConfig()!=null) {
			BuildGoalConfig goalCfg=null;
			
			if (goal!=null && project.getProjectConfig().getGoalsConfig().getGoals()!=null) {
				for (BuildGoalConfig cfg:project.getProjectConfig().getGoalsConfig().getGoals()) {
					if (cfg.getName().equals(goal)) {
						goalCfg=cfg;
					}
				}
			}
			if (goalCfg!=null) {
				taskCtx.setGoalConfig(goalCfg);
			}
				
		}
		
		LOG.debug("Runner successfully prepared: {}",runner.getClass().getName());
		return taskCtx;
	}

	private List<ITaskRunner<? extends BuildTask>> createRunners(List<BuildTask> tasks) {
		if (tasks==null||tasks.isEmpty()) {
			throw new IllegalArgumentException("Empty or null task list");
		}
		
		List<ITaskRunner<? extends BuildTask>> ret=new ArrayList<>();
		for (BuildTask t:tasks) {
			Task ann = t.getClass().getAnnotation(Task.class);
			if (ann==null) {
				throw new IllegalArgumentException(String.format("Task %s does not have the @Task annotation",t.getClass().getName()));
			}
			Class<? extends ITaskRunner<? extends BuildTask>> runnerClass = ann.runnerClass();
			if (runnerClass==null) {
				throw new IllegalArgumentException(String.format("Task %s does not have a runnerClass specified in the @Task annotation",t.getClass().getName()));
			}
			
			try {
				ITaskRunner<? extends BuildTask> r = applicationContext.getBean(runnerClass);
				ret.add(r);
			} catch (Exception e) {
				LOG.error("Failed to instantiate runner: "+runnerClass.getName(),e);
				System.out.println("Task runner instantiation error: "+runnerClass.getName());
				e.printStackTrace();
				throw new BuildException("Failed to instantiate runner: "+runnerClass.getName(),e);
			}
		}
		return ret;
	}

	/**
	 * Requests the cancellation of a build
	 * @param buildId Build ID
	 */
	public void cancelBuild(String buildId) {
		LOG.info("Requesting build cancellation: {}",buildId);
		buildService.cancelBuild(buildId);
	}

	
	
}//end ProjectBuilder