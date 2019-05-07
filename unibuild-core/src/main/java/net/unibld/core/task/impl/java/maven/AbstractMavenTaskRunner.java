package net.unibld.core.task.impl.java.maven;

import net.unibld.core.config.InstallationType;
import net.unibld.core.service.BuildTestService;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.ThirdPartyTaskRunner;
import net.unibld.core.test.TestResults;
import net.unibld.core.util.PlatformHelper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Base class for Maven task runners
 * @author andor
 *
 * @param <T> Generic Maven task parameter
 */
public abstract class AbstractMavenTaskRunner<T extends MavenTask> extends ThirdPartyTaskRunner<T> {
	@Autowired
	private SurefireTestResultParser surefireParser;
	@Autowired
	private BuildTestService buildTestService;
	
	@Override
	protected ExecutionResult execute(T task) {
		String baseDirectory = task.getBaseDirectory();
		logTask("Executing Maven in {0} with arguments: {1}...",baseDirectory!=null?baseDirectory:".", getArguments(task));
		
		ExecutionResult ret = super.execute(task);
		try {
			TestResults results = surefireParser.parseTestResults(FilenameUtils.concat(baseDirectory, "target/surefire-reports"));
			if (results!=null) {
				buildTestService.saveTestResults(task.getContext().getBuildId(), task, results);
			}
		} catch (Exception ex) {
			LoggerFactory.getLogger(getClass()).error("Failed to parse test results",ex);
		}
		return ret;
	}


	@SuppressWarnings("rawtypes")
	protected Map getEnvironment(T task) {
		Map<String,String> env=new HashMap<>();
		
		if (!StringUtils.isEmpty(task.getJavaHome())) {
			env.put("JAVA_HOME", task.getJavaHome());
		}
		if (!StringUtils.isEmpty(task.getM2Home())) {
			env.put("M2_HOME", task.getM2Home());
		}
		return env;
	}
	


	@Override
	protected String getExecutableName(T task) {
		return PlatformHelper.isWindows()?"mvn.bat":"mvn";
		
	}



	@Override
	protected String getArguments(T task) {
		StringBuilder b=new StringBuilder();
		if (task.getOptions()!=null) {
			b.append(task.getOptions());
			b.append(' ');
			if (task.isUpdating()&&!b.toString().contains("-U")) {
				b.append("-U ");
			}
		} else {
			if (task.isUpdating()) {
				b.append("-U ");
			}
		}
		if (task.getGoals()!=null) {
			b.append(task.getGoals());
			b.append(' ');
		}
		addVmArgs(task,b);
		
		return b.toString().trim();
	}

	protected void addVmArgs(T task,StringBuilder b) {
		if (task.getProperties()!=null&&task.getProperties().trim().length()>0) {
			String[] split = task.getProperties().split(";");
			for (String prop:split) {
				b.append("-D");
				b.append(prop.replace("\\", "/"));
				b.append(' ');
			}
			if (task.isSkippingTests()&&!b.toString().contains("-Dmaven.test.skip")) {
				b.append("-Dmaven.test.skip=true ");
			}
			if (task.isSkippingSurefireTests()&&!b.toString().contains("-DskipTests")) {
				b.append("-DskipTests=true ");
			}
			
		} else {
			if (task.isSkippingTests()) {
				b.append("-Dmaven.test.skip=true ");
			}
			if (task.isSkippingSurefireTests()) {
				b.append("-DskipTests=true ");
			}
		}
	}





	@Override
	protected String getExecutableFile(T task) {
		
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


}
