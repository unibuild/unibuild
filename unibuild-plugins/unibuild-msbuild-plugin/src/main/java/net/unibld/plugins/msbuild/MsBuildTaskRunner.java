package net.unibld.plugins.msbuild;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.AbstractExecTaskRunner;

@Component
public class MsBuildTaskRunner extends AbstractExecTaskRunner<MsBuildTask>{
	private static final Logger LOGGER=LoggerFactory.getLogger(MsBuildTaskRunner.class);

	@Override
	protected ExecutionResult execute(MsBuildTask task) {
		String baseDirectory = task.getBaseDirectory();
		logTask("Executing MSBuild in {0} with arguments: {1}...",baseDirectory!=null?baseDirectory:".", getArguments(task));
		
		String exe = getExecutableFile(task);
		
		task.setPath(exe);
		return super.execute(task);
	}
	@Override
	protected String getTaskName() {
		return "msbuild";
	}
	
	protected void validate(MsBuildTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		
		if (StringUtils.isEmpty(task.getBaseDirectory())) {
			throw new BuildException("Project directory not specified");
		}
		if (StringUtils.isEmpty(task.getProject())) {
			throw new BuildException("Project file not specified");
		}
		if (StringUtils.isEmpty(task.getTarget())) {
			throw new BuildException("Target not specified");
		}

	}

	@Override
	protected String getArguments(MsBuildTask task) {
		StringBuilder b=new StringBuilder();
		
		if (!StringUtils.isEmpty(task.getTarget())) {
			b.append("/t:");
			b.append(task.getTarget());
			b.append(' ');
		}
		if (!StringUtils.isEmpty(task.getConfiguration())) {
			b.append("/p:Configuration=");
			b.append(task.getConfiguration());
			b.append(' ');
		}
		
		if (!StringUtils.isEmpty(task.getPlatform())) {
			b.append("/p:Platform=");
			b.append(task.getPlatform());
			b.append(' ');
		}
		
		if (!StringUtils.isEmpty(task.getParameters())) {
			String[] params = task.getParameters().split(";");
			for (String param:params) {
				String[] keyVal = param.split("=");
				if (keyVal.length!=2) {
					throw new BuildException("Invalid parameters format: "+task.getParameters());
				}
				
				b.append("/p:");
				b.append(keyVal[0]);
				b.append("=");
				b.append(keyVal[1]);
				b.append(' ');
			}
		}
		return b.toString().trim();
	}

	
	
	protected String getExecutableFile(MsBuildTask task) {
		File binDir = null;
		if (!StringUtils.isEmpty(task.getFrameworkDir())) {
			binDir=new File(task.getFrameworkDir());
		}
		
		if (binDir!=null) {
			return escapeWindowsSpaces(FilenameUtils.concat(binDir.getAbsolutePath(),getExecutableName(task)));
		} else {
			return getExecutableName(task);
		}
		
	}
	
	protected String getExecutableName(MsBuildTask task) {
		return "msbuild.exe";
	}
	
}
