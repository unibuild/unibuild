package net.unibld.core.task.impl.sys;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;



/**
 * Copies a file or a directory or the contents of a directory to a specified 
 * target directory or target file (in the case of files)
 * @author andor
 *
 */
@Task(name="cp",runnerClass=CpTaskRunner.class)
public class CpTask extends BuildTask {
	private String target;
	private String source;
	private String targetFile;
	
	private boolean overwrite=true;
	/**
	 * Default constructor
	 */
	public CpTask(){

	}
	
	/**
	 * @return Target dir path
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target Target dir path
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return Source file path
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source Source file path
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return True if the target file should be overwritten if it exists. Only applies to copying files.
	 * The default value is true.
	 */
	public boolean isOverwrite() {
		return overwrite;
	}

	/**
	 * @param overwrite True if the target file should be overwritten if it exists. Only applies to copying files.
	 * The default value is true.
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * @return Target file path (ignored if target specified)
	 */
	public String getTargetFile() {
		return targetFile;
	}

	/**
	 * @param targetFile Target file path (ignored if target specified)
	 */
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

	
		
}