package net.unibld.plugins.android;

import net.unibld.core.build.BuildException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Executes an Android SDK adb comannd.<br>
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:28
 */
@Component
public class AndroidAdbInstallTaskRunner extends AndroidAdbTaskRunner<AndroidAdbInstallTask> {
	private static final Logger LOG=LoggerFactory.getLogger(AndroidAdbInstallTaskRunner.class);
	


	@Override
	protected String getTaskName() {
		return "android-adb-install";
	}


	@Override
	protected String getArguments(AndroidAdbInstallTask task) {
		if (task.getApkFile()==null) {
			throw new BuildException("APK file not specified");
		}
		
		if (!hasAnyOptions(task)) {
			return "install "+task.getApkFile();
		} else {
			return String.format("install %s %s",getOptions(task),task.getApkFile());
		}
	}





	private String getOptions(AndroidAdbInstallTask task) {
		StringBuilder b=new StringBuilder();
		int i=0;
		
		if (task.isAllowDowngrade()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-d");
			i++;
		}
		if (task.isAllowTestApks()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-t");
			i++;
		}
		
		if (task.isInstallOnInternalMemory()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-f");
			i++;
		}
		
		if (task.isInstallOnSharedStorage()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-s");
			i++;
		}
		if (task.isInstallWithForwardLock()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-l");
			i++;
		}
		if (task.isReinstallExisting()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-r");
			i++;
		}
		if (!StringUtils.isEmpty(task.getInstallerPackageName())) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-i ");
			b.append(task.getInstallerPackageName());
			i++;
		}
		return b.toString();
	}





	private boolean hasAnyOptions(AndroidAdbInstallTask task) {
		return task.isAllowDowngrade()||task.isAllowTestApks()||task.isInstallOnInternalMemory()||
				task.isInstallOnSharedStorage()||task.isInstallWithForwardLock()||task.isReinstallExisting()||
				!StringUtils.isEmpty(task.getInstallerPackageName());
	}





	@Override
	protected String getCommand() {
		return "install";
	}










	

}//end AndroidAdbTaskRunner