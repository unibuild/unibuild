package net.unibld.core.task.impl.sys;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;



/**
 * Creates a directory on the file system
 * @author andor
 *
 */
@Task(name="mkdir",runnerClass=MkdirTaskRunner.class)
public class MkdirTask extends BuildTask {

	
	private static final long serialVersionUID = -1127692726213552416L;
	
	private String path;
	
	/**
	 * @return Path to create
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path Path to create
	 */
	public void setPath(String path) {
		this.path = path;
	}

	

	
}//end MkdirTask