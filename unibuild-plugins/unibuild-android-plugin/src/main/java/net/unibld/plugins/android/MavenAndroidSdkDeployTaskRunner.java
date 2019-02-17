package net.unibld.plugins.android;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.ExecutionResult;

@Component
public class MavenAndroidSdkDeployTaskRunner extends AbstractMavenAndroidTaskRunner<MavenAndroidSdkDeployTask> {
	private static final Logger LOG=LoggerFactory.getLogger(MavenAndroidSdkDeployTaskRunner.class);
	
	
	@Override
	protected ExecutionResult execute(MavenAndroidSdkDeployTask task) {
		if (task.getExtra()==null&&task.getSdkVersion()==null) {
			throw new IllegalArgumentException("Neither Android SDK version nor extra specified");
		}
		
		String sdkDir=task.getSdkDir();
		if (sdkDir==null) {
			throw new BuildException("SDK dir not defined");
		}
		
		String sdkDeployerDir=task.getSdkDeployerDir();
		
		if (sdkDeployerDir==null) {
			throw new IllegalArgumentException("Android SDK deployer directory could not be detected");
		}
		
		//ExecutionResult sdkRes= checkSdk(sdkDeployerDir);
		//if (!sdkRes.isSuccess()) {
		//	return sdkRes;
		//}
		if (task.getExtra()!=null) {
			sdkDeployerDir=FilenameUtils.concat(sdkDeployerDir, FilenameUtils.concat("extras",task.getExtra()));
			logTask("Checking Maven Android SDK extra {0} in: {1}...",task.getExtra(),sdkDeployerDir);
		} else {
		
			logTask("Checking Maven Android SDK {0} in: {1}...",task.getSdkVersion(),sdkDeployerDir);
		}
			
		
		task.setBaseDirectory(sdkDeployerDir);
		
		return super.executeInDirectory(task);
	}

	@Override
	protected String getArguments(MavenAndroidSdkDeployTask task) {
		StringBuilder b=new StringBuilder();
		if (task.getExtra()==null) {
			b.append("install -P ");
			b.append(task.getSdkVersion());
		} else {
			b.append("clean install -N");
		}
		b.append(' ');
		addVmArgs(task,b);
		return b.toString();
	}

	@Override
	protected String getTaskName() {
		return "maven-android-sdk-deploy";
	}


}
