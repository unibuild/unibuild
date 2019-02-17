package net.unibld.plugins.android;

import net.unibld.core.task.annotations.Task;

@Task(name="maven-android-sdk-deploy",runnerClass=MavenAndroidSdkDeployTaskRunner.class)
public class MavenAndroidSdkDeployTask extends MavenAndroidTask {
	private String sdkVersion;
	private String sdkDeployerDir;
	private String extra;
	
	
	public String getSdkVersion() {
		return sdkVersion;
	}
	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}
	public String getSdkDeployerDir() {
		return sdkDeployerDir;
	}
	public void setSdkDeployerDir(String sdkDeployerDir) {
		this.sdkDeployerDir = sdkDeployerDir;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
}
