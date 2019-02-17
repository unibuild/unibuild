package net.unibld.core.task.impl.sys;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;



/**
 * A task that performs a chmod command on Unix-based systems using {@link ChmodTaskRunner}
 * @author andor
 *
 */
@Task(name="chmod",runnerClass=ChmodTaskRunner.class)
public class ChmodTask extends BuildTask {
	
	private static final long serialVersionUID = -2558755160787619678L;
	private String path;
	private String mode;
	private String recursive;
	
	/**
	 * @return The path to chmod
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path The path to chmod
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return The mode to set in octal string
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * @param mode The mode to set in octal string
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	/**
	 * @return 'true' if chmod should be recursive (ignored if path does not denote
	 * a directory)
	 */
	public String getRecursive() {
		return recursive;
	}
	/**
	 * @param recursive 'true' if chmod should be recursive (ignored if path does not denote
	 * a directory)
	 */
	public void setRecursive(String recursive) {
		this.recursive = recursive;
	}
	/**
	 * @return True if chmod should be recursive (ignored if path does not denote
	 * a directory)
	 */
	public boolean isRecursive() {
		return recursive!=null&&"true".equals(recursive);
	}
	
}//end ChmodTask