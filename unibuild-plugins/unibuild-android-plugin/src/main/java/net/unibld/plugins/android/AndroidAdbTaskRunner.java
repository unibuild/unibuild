package net.unibld.plugins.android;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.AbstractExecTaskRunner;
import net.unibld.core.util.PlatformHelper;


/**
 * Executes an Android SDK adb comannd.<br>
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:28
 */
public abstract class AndroidAdbTaskRunner<T extends AndroidAdbTask> extends AbstractExecTaskRunner<T> {
	

	protected String getExecutableFile(T task) {
		String sdkDir=null;
		
		if (task.getSdkDir()==null) {
			throw new BuildException("SDK dir not defined");
		} else {
			sdkDir=task.getSdkDir();
		}
		
		
		LoggerFactory.getLogger(getClass()).info("Using SDK dir: {}...",sdkDir);
		String ret = FilenameUtils.concat(FilenameUtils.concat(sdkDir, "platform-tools"),getExecutableName());
		if (ret.contains(" ")) {
			ret=String.format("\"%s\"",ret);
		}
		return ret;
	}

	protected String getExecutableName() {
		return PlatformHelper.isWindows()?"adb.exe":"adb";
	}





	@Override
	protected ExecutionResult execute(T task) {
		
		String exe = getExecutableFile(task);
		LoggerFactory.getLogger(getClass()).info("Executing Android ADB task with executable: {} ...",exe);
		logTask(String.format("Executing adb %s...", getCommand()));
		task.setPath(exe);
		
		
		
		ExecutionResult res = execCmd(task);
		return res;
	}





	protected abstract String getCommand();






	

}//end AndroidAdbTaskRunner