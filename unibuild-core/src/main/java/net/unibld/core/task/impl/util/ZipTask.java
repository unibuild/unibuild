package net.unibld.core.task.impl.util;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;

@Task(name="zip",runnerClass=ZipTaskRunner.class)
public class ZipTask extends BuildTask {
	private String output;
	
	private String files;
	private String dir;
	private String dirExcludes;
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getFiles() {
		return files;
	}
	public void setFiles(String files) {
		this.files = files;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getDirExcludes() {
		return dirExcludes;
	}
	public void setDirExcludes(String dirExcludes) {
		this.dirExcludes = dirExcludes;
	}

}
