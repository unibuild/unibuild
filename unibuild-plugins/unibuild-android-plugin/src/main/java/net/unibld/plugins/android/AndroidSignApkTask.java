package net.unibld.plugins.android;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.java.SignJarTask;

/**
 * A task that signs an Android APK using jarsigner and zipaligns it if configured using the Android SDK bundled
 * zipalign tool.
 * @author andor
 *
 */
@Task(name="android-signapk",runnerClass=AndroidSignApkTaskRunner.class)
public class AndroidSignApkTask extends SignJarTask {
	
	private static final long serialVersionUID = 6623042334784373823L;
	private boolean zipAlign=true;
	private String sdkDir;
	private String antProperties;
	private boolean alreadySigned;
	
	

	/**
	 * @return True if zipalign should be performed
	 */
	public boolean isZipAlign() {
		return zipAlign;
	}

	/**
	 * @param zipAlign True if zipalign should be performed
	 */
	public void setZipAlign(boolean zipAlign) {
		this.zipAlign = zipAlign;
	}

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

	/**
	 * @return The path of ant.properties if the signing keystore is configured in ant.properties file, null otherwise
	 */
	public String getAntProperties() {
		return antProperties;
	}

	/**
	 * @param antProperties The path of ant.properties if the signing keystore is configured in ant.properties file, null otherwise
	 */
	public void setAntProperties(String antProperties) {
		this.antProperties = antProperties;
	}

	/**
	 * @return True if the input APK is already signed and needs extract
	 */
	public boolean isAlreadySigned() {
		return alreadySigned;
	}

	/**
	 * @param alreadySigned True if the input APK is already signed and needs extract
	 */
	public void setAlreadySigned(boolean alreadySigned) {
		this.alreadySigned = alreadySigned;
	}
}
