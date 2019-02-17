	package net.unibld.core.task.impl.java.gradle;

import net.unibld.core.config.InstallationType;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.ThirdPartyTaskRunner;
import net.unibld.core.util.PlatformHelper;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;


/**
 * Executes a {@link GradleTask} using Gradle and Apache Commons Exec.<br>
 * Stores the output of Gradle in a log file called maven.[TIMESTAMP].log in the
 * logs directory.<br>
 * Since Gradle works by executing gradle in the current folder, we need to cd to the
 * project directory that has a build.gradle file in it, using GradleTask's baseDirectory attribute.
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:28
 */
@Component
public class GradleTaskRunner extends ThirdPartyTaskRunner<GradleTask> {
	
	@Override
	protected ExecutionResult execute(GradleTask task) {
		String baseDirectory = task.getBaseDirectory();
		logTask("Executing Gradle in {0} with arguments: {1}...",
				baseDirectory != null ? baseDirectory : ".", getArguments(task));
		
		return super.execute(task);
	}

	@Override
	protected String getExecutableName(GradleTask task) {
		return PlatformHelper.isWindows()?"gradle.bat":"gradle";
		
	}


	@Override
	protected String getArguments(GradleTask task) {
		StringBuilder b=new StringBuilder();
		if (task.getOptions()!=null) {
			b.append(task.getOptions());
			b.append(' ');
			
		} 
		if (task.getTask()!=null) {
			b.append(task.getTask());
			b.append(' ');
		}
		if (task.getProperties()!=null&&task.getProperties().trim().length()>0) {
			String[] split = task.getProperties().split(";");
			for (String prop:split) {
				b.append("-D");
				b.append(prop.replace("\\", "/"));
				b.append(' ');
			}
			
		} 
		
		return b.toString();
	}




	@Override
	protected String getExecutableFile(GradleTask task) {
		
		if (task.getInstallationType()==null) {
			prepareTaskWithDefaultInstallationType(task);
		} 
		boolean useAbsolutePath = task.getInstallationType()==InstallationType.folder;
		if (useAbsolutePath&&task.getInstallationDir()!=null) {
			return FilenameUtils.concat(task.getInstallationDir(),getExecutableName(task));
		}
		
		if (task.getInstallationType()==InstallationType.path) {
			return getExecutableName(task);
		}
		return super.getExecutableFile(task);
	}


	@Override
	protected String getTaskName() {
		return "gradle";
	}
}//end GradleTaskRunner