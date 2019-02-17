package net.unibld.plugins.unity;

import net.unibld.core.task.impl.ExecTask;

/**
 * An abstract task that executes a Unity command
 * @author andor
 *
 */
public abstract class UnityTask extends ExecTask {
	
	private String unityDir;
	
	private String outputFile;
	private String projectPath;
	private boolean batchMode=true;
	private boolean quit=true;
	
		
	/**
	 * @return Unity install path configured in the task (overriding global config value)
	 */
	public String getUnityDir() {
		return unityDir;
	}

	/**
	 * @param unityDir Unity install path configured in the task (overriding global config value)
	 */
	public void setUnityDir(String unityDir) {
		this.unityDir = unityDir;
	}


	public String getOutputFile() {
		return outputFile;
	}


	public void setOutputFile(String target) {
		this.outputFile = target;
	}


	public boolean isBatchMode() {
		return batchMode;
	}


	public void setBatchMode(boolean batchMode) {
		this.batchMode = batchMode;
	}


	public String getProjectPath() {
		return projectPath;
	}


	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}


	public boolean isQuit() {
		return quit;
	}


	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	
}
