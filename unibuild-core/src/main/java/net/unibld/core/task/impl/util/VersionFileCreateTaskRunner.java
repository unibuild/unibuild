package net.unibld.core.task.impl.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.version.VersionContext;
import net.unibld.core.util.version.VersionHelper;

@Component
public class VersionFileCreateTaskRunner extends BaseTaskRunner<VersionFileCreateTask>{
	private static final Logger LOG=LoggerFactory.getLogger(VersionFileCreateTaskRunner.class);
	
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "version-file-create";
	}



	@Override
	protected ExecutionResult execute(VersionFileCreateTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		if (StringUtils.isEmpty(task.getPath())) {
			throw new IllegalStateException("Output path was not specified");
		}
		logTask("Saving version info to: {0}...", task.getPath());
			
		String version=null;
		if (task.getVersion()!=null&&task.getVersion().trim().length()>0) {
			version=task.getVersion().trim();
		} else {
		
			boolean pom = !StringUtils.isEmpty(task.getPomFile());
			boolean buildNum = !StringUtils.isEmpty(task.getBuildNumberProperties());
			if (pom||buildNum) {
				VersionContext ctx=new VersionContext();
				ctx.setPom(pom);
				ctx.setBuildNum(buildNum);
				ctx.setPomFile(task.getPomFile());
				ctx.setBuildNumberProperties(task.getBuildNumberProperties());
				ctx.setBuildNumberPropertyName(task.getBuildNumberPropertyName());
				ctx.setProjectConfig(getProjectConfig(task));
				ctx.setTaskLogger(this);
				version=VersionHelper.getVersionFromPomOrProperties(ctx);
				
			} else {
				throw new BuildException("Version must be specified explicitly or by using attributes 'pomFile' or 'useProjectVersion'");
			}
		
		}
		
		if (version==null) {
			throw new IllegalStateException("Version could not be determined");
		}
		
		File outFile=new File(task.getPath());
		try {
			FileUtils.write(outFile, version, "UTF-8");
			return ExecutionResult.buildSuccess();
		} catch (IOException e) {
			LOG.error("Failed to write version file: "+task.getPath(),e);
			throw new BuildException("Failed to write version file: "+task.getPath(),e);
		}
		
	}
	
	

}
