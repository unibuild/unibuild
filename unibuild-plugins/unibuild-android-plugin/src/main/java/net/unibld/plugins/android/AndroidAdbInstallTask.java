package net.unibld.plugins.android;

import net.unibld.core.task.annotations.Task;

/**
 * A task that installs an APK to an attached Android device.
 * @author andor
 *
 */
@Task(name="android-adb-install",runnerClass=AndroidAdbInstallTaskRunner.class)
public class AndroidAdbInstallTask extends AndroidAdbTask {
	
	
	private String apkFile;
	
	private boolean reinstallExisting=true;
	private boolean installWithForwardLock=false;
	private boolean allowTestApks=false;
	private boolean installOnSharedStorage=false;
	private boolean installOnInternalMemory=false;
	private boolean allowDowngrade=false;
	private String installerPackageName;
	/**
	 * Default constructor, declaring android-sdk dependency
	 */
	public AndroidAdbInstallTask() {
		super();
	}

	
	

	public String getApkFile() {
		return apkFile;
	}


	public void setApkFile(String apkFile) {
		this.apkFile = apkFile;
	}


	
	
	

	/**
	 * Switch -r: Reinstall an exisiting app, keeping its data.
	 * @return True if switch is present
	 */
	public boolean isReinstallExisting() {
		return reinstallExisting;
	}




	/**
	 * Switch -r: Reinstall an exisiting app, keeping its data.
	 * @param reinstallExisting True if switch is present
	 */
	public void setReinstallExisting(boolean reinstallExisting) {
		this.reinstallExisting = reinstallExisting;
	}




	/**
	 * Switch -l: Install the package with forward lock.
	 * @return True if switch is present
	 */
	public boolean isInstallWithForwardLock() {
		return installWithForwardLock;
	}




	/**
	 * Switch -l: Install the package with forward lock.
	 * @param installWithForwardLock True if switch is present
	 */
	public void setInstallWithForwardLock(boolean installWithForwardLock) {
		this.installWithForwardLock = installWithForwardLock;
	}




	/**
	 * Switch -t: Allow test APKs to be installed.
	 * @return True if switch is present
	 */
	public boolean isAllowTestApks() {
		return allowTestApks;
	}




	/**
	 * Switch -t: Allow test APKs to be installed.
	 * @param allowTestApks True if switch is present
	 */
	public void setAllowTestApks(boolean allowTestApks) {
		this.allowTestApks = allowTestApks;
	}




	/**
	 * Switch -s: Install package on the shared mass storage (such as sdcard).
	 * @return True if switch is present
	 */
	public boolean isInstallOnSharedStorage() {
		return installOnSharedStorage;
	}




	/**
	 * Switch -s: Install package on the shared mass storage (such as sdcard).
	 * @param installOnSharedStorage True if switch is present
	 */
	public void setInstallOnSharedStorage(boolean installOnSharedStorage) {
		this.installOnSharedStorage = installOnSharedStorage;
	}




	/**
	 * Switch -f: Install package on the internal system memory.
	 * @return True if switch is present
	 */
	public boolean isInstallOnInternalMemory() {
		return installOnInternalMemory;
	}




	/**
	 * Switch -f: Install package on the internal system memory.
	 * @param installOnInternalMemory True if switch is present
	 */
	public void setInstallOnInternalMemory(boolean installOnInternalMemory) {
		this.installOnInternalMemory = installOnInternalMemory;
	}




	/**
	 * Switch -d: Allow version code downgrade.
	 * @return True if switch is present
	 */
	public boolean isAllowDowngrade() {
		return allowDowngrade;
	}




	/**
	 * Switch -d: Allow version code downgrade.
	 * @param allowDowngrade True if switch is present
	 */
	public void setAllowDowngrade(boolean allowDowngrade) {
		this.allowDowngrade = allowDowngrade;
	}




	/**
	 * Switch -i <INSTALLER_PACKAGE_NAME>: Specify the installer package name.
	 * @return Installer package name or null
	 */
	public String getInstallerPackageName() {
		return installerPackageName;
	}




	/**
	 * Switch -i <INSTALLER_PACKAGE_NAME>: Specify the installer package name.
	 * @param installerPackageName Installer package name or null
	 */
	public void setInstallerPackageName(String installerPackageName) {
		this.installerPackageName = installerPackageName;
	}

	
}
