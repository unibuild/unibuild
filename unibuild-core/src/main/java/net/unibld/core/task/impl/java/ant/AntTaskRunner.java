package net.unibld.core.task.impl.java.ant;

import net.unibld.core.config.InstallationType;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.ThirdPartyTaskRunner;
import net.unibld.core.util.PlatformHelper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Executes an {@link AntTask} using Apache Ant and Apache Commons Exec.<br>
 * Stores the output of Ant in a log file called ant.[TIMESTAMP].log in the
 * logs directory.<br>
 * Since Ant works by executing mvn in the current folder, we need to cd to the
 * project directory that has build.xml it, using AntTask's baseDirectory attribute.
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:27
 */
@Component
public class AntTaskRunner extends ThirdPartyTaskRunner<AntTask> {
	private static final Logger LOG=LoggerFactory.getLogger(AntTaskRunner.class);


	@Override
	protected String getTaskName() {
		return "ant";
	}
	
	@SuppressWarnings("rawtypes")
	protected Map getEnvironment(AntTask task) {
		Map<String,String> env=new HashMap<>();
		if (!StringUtils.isEmpty(task.getJavaHome())) {
			env.put("JAVA_HOME", task.getJavaHome());
		}
		return env;
	}
	

	@Override
	protected ExecutionResult execute(AntTask task) {
		
		
		String baseDirectory = task.getBaseDirectory();
		if (baseDirectory!=null) {
			if (task.getTarget()!=null) {
				logTask("Executing Ant target in {0}: {1}...", baseDirectory,task.getTarget());
			} else {
				logTask("Executing default Ant target in {0}...", baseDirectory);
				
			}
			LOG.info("Using to base directory: {}",baseDirectory);
			
		} else {
			if (task.getTarget()!=null) {
				logTask("Executing Ant target: {0}...", task.getTarget());
			} else {
				logTask("Executing default Ant target...");
				
			}
		}
		return super.execute(task);
		
	}



	@Override
	protected String getExecutableName(AntTask task) {
		return PlatformHelper.isWindows()?"ant.bat":"ant";
		
	}



	@Override
	protected String getArguments(AntTask task) {
		StringBuilder b=new StringBuilder();
		
		
		if (task.getBuildFile()!=null&&task.getBuildFile().trim().length()>0) {
			b.append("-buildfile ");
			b.append(task.getBuildFile());
			b.append(' ');
		}
		if (task.getLib()!=null&&task.getLib().trim().length()>0) {
			b.append("-lib ");
			b.append(task.getLib());
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
		if (task.getTarget()!=null) {
			b.append(task.getTarget());
		}
		return b.toString();
	}



	@Override
	protected String getExecutableFile(AntTask task) {
		boolean useAbsolutePath = task.getInstallationType()==InstallationType.folder;
		if (useAbsolutePath&&task.getInstallationDir()!=null) {
			return FilenameUtils.concat(task.getInstallationDir(),getExecutableName(task));
		}
		
		if (task.getInstallationType()==InstallationType.path) {
			return getExecutableName(task);
		}
		return super.getExecutableFile(task);
	}


	
}//end AntTaskRunner