package net.unibld.core.task.impl;

import net.unibld.core.task.ExecutionResult;

import org.springframework.stereotype.Component;

/**
 * A task runner that executes an OS command defined in an {@link ExecTask}.
 * @author andor
 *
 */
@Component
public class ExecTaskRunner extends AbstractExecTaskRunner<ExecTask>{
	@Override
	protected String getTaskName() {
		return "exec";
	}

	@Override
	protected ExecutionResult execCmd(ExecTask task) {
		String line=null;
		if (task.getArgs()!=null) {
			line = String.format("%s %s",task.getPath() , task.getArgs());
		} else {
			line = task.getPath();
		}
		logTask("Executing an OS command: {0}...", line);
		
		return super.execCmd(task);
	}
}
