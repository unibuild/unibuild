package net.unibld.plugins.android;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ExecTask;
import net.unibld.core.task.impl.java.SignJarTask;

/**
 * An abstract task that executes an Android ADB command
 * @author andor
 *
 */
public abstract class AndroidAdbTask extends ExecTask {
	
	private String sdkDir;
	
		
	/**
	 * @return Android SDK path configured in the task (overriding global config value)
	 */
	public String getSdkDir() {
		return sdkDir;
	}

	/**
	 * @param sdkDir Android SDK path configured in the task (overriding global config value)
	 */
	public void setSdkDir(String sdkDir) {
		this.sdkDir = sdkDir;
	}

	
}
