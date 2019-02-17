package net.unibld.core.scm;

import java.io.Serializable;

import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.ExecTask;

/**
 * An generic interface for an SCM command
 * @author andor
 *
 * @param <T> Generic task parameter that extends {@link ExecTask}
 */
public interface IScmCommand<T extends ExecTask> extends Serializable {
	/**
	 * @return Arguments as a single string
	 */
	public String getArguments();
	/**
	 * @return Executable file (such as svn.exe)
	 */
	public String getExecutable();
	/**
	 * @return SCM command as string
	 */
	public String getCommand();
	
	/**
	 * Prepares the command before executing
	 * @param attributeContainer Attribute container
	 * @param task Invoking task
	 */
	public void prepare(IBuildContextAttributeContainer attributeContainer,T task);
	/**
	 * A method that is executed after the execution of the command itself
	 * @param res The result of the execution of the command
	 */
	public void postExecute(ExecutionResult res);
}
