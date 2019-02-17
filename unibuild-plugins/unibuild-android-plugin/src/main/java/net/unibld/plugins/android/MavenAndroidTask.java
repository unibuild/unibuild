package net.unibld.plugins.android;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.java.maven.MavenTask;

@Task(name="maven-android",runnerClass=MavenAndroidTaskRunner.class)
public class MavenAndroidTask extends MavenTask {
	private String sdkDir;
	private String sdkDeployerDir;
	
	
	public String getSdkDir() {
		return sdkDir;
	}
	public void setSdkDir(String sdkDir) {
		this.sdkDir = sdkDir;
	}
	public String getSdkDeployerDir() {
		return sdkDeployerDir;
	}
	public void setSdkDeployerDir(String sdkDeployerDir) {
		this.sdkDeployerDir = sdkDeployerDir;
	}
	
}
