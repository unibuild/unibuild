package net.unibld.core.task.impl.java.buildnumber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.BuildNumberUtils;

/**
 * A task runner that increases the build number in a Java/Maven generated buildNumber.properties file
 * @author andor
 *
 */
@Component
public class BuildNumberIncreaseTaskRunner extends BaseTaskRunner<BuildNumberIncreaseTask> {
	private static final Logger LOG=LoggerFactory.getLogger(BuildNumberIncreaseTaskRunner.class);

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "buildnumber-inc";
	}

	
	@Override
	protected ExecutionResult execute(BuildNumberIncreaseTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		logTask("Increasing build number in: {0}...", task.getBuildNumberProperties());
			
		File propsFile=new File(task.getBuildNumberProperties());
		try {
			if (propsFile.exists()) {
				increaseBuildNumber(task,propsFile);
			} else {
				createBuildNumber(task,propsFile);
			}
			return ExecutionResult.buildSuccess();
		} catch (Exception e) {
			logError("Could increase build number: "+task.getBuildNumberProperties(),e);
			return ExecutionResult.buildError("Could increase build number: "+task.getBuildNumberProperties(), e);
		}
		
		
	}

	private void createBuildNumber(BuildNumberIncreaseTask task,File propsFile) {
		Properties p=new Properties();
		BuildNumberUtils.setBuildNumberInProperties(p,task.getBuildNumberPropertyName(),1);
		
		
		writeProperties(propsFile, p);
	}

	private void increaseBuildNumber(BuildNumberIncreaseTask task,File propsFile) {
		Properties p=new Properties();
		readProperties(propsFile, p);
		

		BuildNumberUtils.incBuildNumberInProperties(p,task.getBuildNumberPropertyName());
	
		
		writeProperties(propsFile, p);
	}

	protected void readProperties(File propsFile, Properties p) {
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(propsFile);
			p.load(fis);
			
			
		} catch (IOException ex) {
			logError("Failed to increase build number: "+propsFile.getAbsolutePath(), ex);
			throw new BuildException("Failed to increase build number: "+propsFile.getAbsolutePath(), ex);
		} finally {
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					logError("Failed to close build number input stream: "+propsFile.getAbsolutePath(), e);
				}
			}
		}
	}

	protected void writeProperties(File propsFile, Properties p) {
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(propsFile);
			p.store(fos,"Updated automatically by UniBuild");
		} catch (Exception ex) {
			logError("Failed to increase build number: "+propsFile.getAbsolutePath(), ex);
			throw new BuildException("Failed to increase build number: "+propsFile.getAbsolutePath(), ex);
		} finally {
			if (fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					logError("Failed to close build number output stream: "+propsFile.getAbsolutePath(), e);
				}
			}
		}
	}


}
