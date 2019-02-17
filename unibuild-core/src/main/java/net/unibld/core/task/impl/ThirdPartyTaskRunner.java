package net.unibld.core.task.impl;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import net.unibld.core.config.InstallationType;
import net.unibld.core.task.ExecutionResult;

/**
 * An abstract task runner for third-party applications bundled with unibuild or installed locally.
 * A third-party application can be invoked from an external folder, or can be on the path.
 * @author andor
 * @param <T> Generic task parameter
 *
 */
public abstract class ThirdPartyTaskRunner<T extends ThirdPartyExecTask> extends AbstractExecTaskRunner<T> {
	
	@Override
	protected ExecutionResult execute(T task) {
		return doExecute(task);
		
	}
	protected ExecutionResult doExecute(T task) {
		String exe = getExecutableFile(task);
		
		task.setPath(exe);
		return execCmd(task);
	}
	protected String getExecutableFile(T t) {
		File binDir = null;
		if (t.getInstallationDir()!=null) {
			binDir=new File(t.getInstallationDir());
		}
		
		if (binDir!=null) {
			return escapeWindowsSpaces(FilenameUtils.concat(binDir.getAbsolutePath(),getExecutableName(t)));
		} else {
			return getExecutableName(t);
		}
		
	}
	
	protected abstract String getExecutableName(T task);
	
	protected void prepareTaskWithDefaultInstallationType(T task) {
		task.setInstallationType(InstallationType.path);
		
	}
	
	
}
