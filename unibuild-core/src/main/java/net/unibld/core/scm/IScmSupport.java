package net.unibld.core.scm;

import net.unibld.core.task.ExecutionResult;

/**
 * Interface for a support class for an SCM management tool, such as CVS, SVN or Git.<br>
 * 
 * @author andor
 *
 * @param <C> Generic command base type for a specific SCM tool
 */
public interface IScmSupport<C extends IScmCommand<?>> {
	/**
	 * Creates a specific SCM command object subclassing the generic base command type, C
	 * @param klazz Command class to instantiate
	 * @return Command instance
	 * @throws Exception If any error occurs
	 */
	public <X extends C> X create(Class<X> klazz) throws Exception;
	/**
	 * Executes an SCM command
	 * @param cmd Command to execute
	 * @return Result of the execution
	 */
	public ExecutionResult execute(C cmd);
}
