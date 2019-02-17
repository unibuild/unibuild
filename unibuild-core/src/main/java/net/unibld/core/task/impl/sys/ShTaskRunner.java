package net.unibld.core.task.impl.sys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.ShellTaskRunner;


/**
 * A task runner that runs an .sh script file
 * @author andor
 *
 */
@Component
public class ShTaskRunner extends ShellTaskRunner<ShTask> {
	private static final Logger LOG=LoggerFactory.getLogger(ShTaskRunner.class);
	


	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "sh";
	}




	@Override
	protected ExecutionResult execute(ShTask task) {
		if (task.getPath()==null) {
			throw new BuildException("Path not defined");
		}
		if (task.getArgs()==null) {
			logTask("Executing sh script: {0}...", task.getPath());
		} else {
			logTask("Executing sh script: {0} with args {1}...", task.getPath(),task.getArgs());
		}
		
		return super.execute(task);
	}
}//end CustomNativeTaskRunner