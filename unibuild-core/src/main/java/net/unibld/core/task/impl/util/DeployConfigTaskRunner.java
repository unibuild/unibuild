package net.unibld.core.task.impl.util;

import java.io.File;

import net.unibld.core.config.ProjectConfig;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * A task that deploys a specified folder to the config directory specified in the current {@link ProjectConfig}.
 * @author andor
 *
 */
@Component
public class DeployConfigTaskRunner extends BaseTaskRunner<DeployConfigTask> {
	private static final Logger LOG=LoggerFactory.getLogger(DeployConfigTaskRunner.class);
	
	



	protected ExecutionResult execute(DeployConfigTask task) {
		
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		
		if (task.getSource()==null) {
			throw new IllegalArgumentException("Source was not specified");
		}
		
		logTask("Deploying config to: {0}", task.getConfigDir());
		
		
		File targetFile=new File(task.getConfigDir());
		File sourceFile=new File(task.getSource());
		
		if (!sourceFile.exists()) {
			return ExecutionResult.buildError("Source file does not exist: "+task.getSource());
		}
		
		
		
		
		targetFile.mkdirs();
		if (sourceFile.isFile()) {
			throw new IllegalArgumentException("Source is not a directory");
		} else {
			try {
				
				LOG.debug("Copying config source dir {} to target dir: {}...",sourceFile.getAbsolutePath(),targetFile.getAbsolutePath());
				FileUtils.copyDirectory(sourceFile, targetFile);
				LOG.info("Copied config source dir {} to target dir: {}...",sourceFile.getAbsolutePath(),targetFile.getAbsolutePath());
			} catch (Exception ex) {
				String msg = String.format("Failed to copy config source %s to target: %s",sourceFile.getAbsolutePath(),targetFile.getAbsolutePath());
				LOG.error(msg,ex);
				return ExecutionResult.buildError(msg,ex);
			}
		}
		
		
		return ExecutionResult.buildSuccess();
		
	}

	

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "deploy-config";
	}


}//end CustomNativeTaskRunner