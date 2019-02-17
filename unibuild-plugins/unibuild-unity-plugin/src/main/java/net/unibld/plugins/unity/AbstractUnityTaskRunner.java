package net.unibld.plugins.unity;

import java.io.File;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.AbstractExecTaskRunner;
import net.unibld.core.util.PlatformHelper;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;


/**
 * A task runner for executing a Unity3D command
 * @author andor
 * @version 1.0
 * @param <T> Generic task parameter that extends {@link UnityTask}
 * 
 */
public abstract class AbstractUnityTaskRunner<T extends UnityTask> extends AbstractExecTaskRunner<T> {
	

	protected String getExecutableFile(T task) {
		String unityDir=null;
	/*if (task.getUnityDir()==null) {
			unityDir=AndroidHelper.detectSdkDir(getGlobalConfig());
		} else {
			unityDir=task.getUnityDir();
		}*/
		unityDir=task.getUnityDir();
		
		if (unityDir==null) {
			throw new BuildException("Unity dir not defined");
		}
		
		LoggerFactory.getLogger(getClass()).info("Using Unity dir: {}...",unityDir);
		String ret = FilenameUtils.concat(FilenameUtils.concat(unityDir, "Editor"),getExecutableName());
		if (ret.contains(" ")) {
			ret=String.format("\"%s\"",ret);
		}
		return ret;
	}

	protected String getExecutableName() {
		return PlatformHelper.isWindows()?"Unity.exe":"Unity";
	}



	protected String getOptions(T task) {
		StringBuilder b=new StringBuilder();
		int i=0;
		
		
		if (task.isBatchMode()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-batchMode");
			i++;
		}
		if (task.isQuit()) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-quit");
			i++;
		}
		
		if (!StringUtils.isEmpty(task.getProjectPath())) {
			if (i>0) {
				b.append(' ');
			}
			b.append("-projectPath ");
			b.append(task.getProjectPath());
			i++;
		}


		return b.toString();
	}



	@Override
	protected ExecutionResult execute(T task) {
			
		String exe = getExecutableFile(task);
		LoggerFactory.getLogger(getClass()).info("Executing Unity task with executable: {} ...",exe);
		logTask(String.format("Executing Unity -%s...", getCommand(task)));
		task.setPath(exe);
		
		String target=task.getOutputFile();
		if (!StringUtils.isEmpty(target)) {
			File targetFile=new File(target);
			if (targetFile.getParentFile()!=null) {
				targetFile.getParentFile().mkdirs();
			}
		}
 		
		
		ExecutionResult res = execCmd(task);
		return res;
	}


	protected String getArguments(T task) {
		if (task.getOutputFile()==null) {
			throw new BuildException("Target file not specified");
		}
		
		if (!hasAnyOptions(task)) {
			return String.format("-%s %s",getCommand(task),task.getOutputFile());
		} else {
			return String.format("%s -%s %s",getOptions(task),getCommand(task), task.getOutputFile());
		}
	}



	protected abstract String getCommand(T task);





	protected boolean hasAnyOptions(T task) {
		
		return task.isQuit()||task.isBatchMode()||!StringUtils.isEmpty(task.getProjectPath());
	}

	




	

}//end AndroidAdbTaskRunner