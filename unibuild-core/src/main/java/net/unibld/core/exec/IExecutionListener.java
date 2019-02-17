package net.unibld.core.exec;


/**
 * A listener interface that declares method for listening to the execution
 * of an external process, typically with commons-exec and {@link CmdExecutor}
 * @author andor
 *
 */
public interface IExecutionListener {

	void logExecution(String line,String output);
	void handleOutput(String output);
}
