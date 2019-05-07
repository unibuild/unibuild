package net.unibld.core.task.impl.util;

import java.io.File;

import net.unibld.core.build.BuildException;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.remote.RemoteBuildClient;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Executes a {@link RemoteBuildTask} on a UniBuild server using RESTful web 
 * services.
 * @author andor
 *
 */
@Component
public class RemoteBuildTaskRunner extends BaseTaskRunner<RemoteBuildTask> {
	private static final Logger LOG=LoggerFactory.getLogger(RemoteBuildTaskRunner.class);
	

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "remote-build";
	}

	@Override
	protected ExecutionResult execute(RemoteBuildTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		if (task.getHost()==null||task.getHost().trim().length()==0) {
			throw new IllegalStateException("Host was null or empty");
		}
		if (task.getBuildDir()==null||task.getBuildDir().trim().length()==0) {
			throw new IllegalStateException("Host was null or empty");
		}
		ProjectConfig pc = getProjectConfig(task);
		if (pc==null) {
			throw new IllegalStateException("Project config was null");			
		}
		String projectName=pc.getProjectName();
		if (projectName==null||projectName.trim().length()==0) {
			throw new IllegalStateException("Project name was null or empty");
		}
		
		if (task.getHost()==null) {
			throw new BuildException("Host was not specified");
		}
		if (task.getTicket()==null) {
			throw new BuildException("Ticket was not specified");
		}
		if (task.getProjectFile()==null) {
			throw new BuildException("Project file was not specified");
		}
		if (task.getGoal()==null) {
			throw new BuildException("Goal was not specified");
		}
		try {
			RemoteBuildClient cl=new RemoteBuildClient(task.getHost(), task.getTicket(),task.getProjectFile());
			if (task.getBuildDir()!=null) {
				cl.setBuildDir(new File(task.getBuildDir()).getAbsolutePath());
			} else {
				cl.setBuildDir(new File(".").getAbsolutePath());
			}
			logTask(String.format("Executing remote build in %s...",cl.getBuildDir()));
			
			cl.setGoal(task.getGoal());
			
			cl.setIncludesString(task.getIncludes());
			cl.setExcludesString(task.getExcludes());
			cl.setOutputDir(task.getOutputDir());
			cl.setOutputFileName(task.getOutputFile());
			boolean result=cl.executeBuild();
			if (result) {
				return ExecutionResult.buildSuccess();
			} else {
				return ExecutionResult.buildError(cl.getErrorMessage());
			}
		} catch (Exception ex) {
			logError("Failed to execute remote build",ex);
			return ExecutionResult.buildError("Failed to execute remote build", ex);
		}
	}
}
