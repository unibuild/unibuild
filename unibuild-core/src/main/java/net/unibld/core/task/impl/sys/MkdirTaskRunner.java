package net.unibld.core.task.impl.sys;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;


/**
 * A task that creates a directory path recursively specified in the path parameter
 * @author andor
 *
 */
@Component
public class MkdirTaskRunner extends BaseTaskRunner<MkdirTask> {
	private static final Logger LOG=LoggerFactory.getLogger(MkdirTaskRunner.class);
	
	
	protected ExecutionResult execute(MkdirTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		if (task.getPath()==null) {
			throw new BuildException("Directory to create was null");
		}
		logTask("Creating directory: {0}...", task.getPath());
		
		File dir=new File(task.getPath());
		boolean res=dir.mkdirs();
		if (res) {
			LOG.info(String.format("%s: %s",res,task.getPath()));
		} else {
			LOG.info(String.format("Directory already exists: %s",task.getPath()));
		}
		return ExecutionResult.buildSuccess();
	}


	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "mkdir";
	}
}//end CustomNativeTaskRunner