package net.unibld.plugins.android;

import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.java.maven.AbstractMavenTaskRunner;

/**
 * Base class for Maven Android task runners.
 * @author andor
 *
 * @param <T> Generic Maven Android task parameter
 */
public abstract class AbstractMavenAndroidTaskRunner<T extends MavenAndroidTask> extends AbstractMavenTaskRunner<T>{
	
	@Override
	protected ExecutionResult execute(T task) {
		
		String sdkDir=task.getSdkDir();
		if (sdkDir==null) {
			throw new IllegalArgumentException("Android SDK directory could not be detected");
		}
		String sdkDeployerDir=task.getSdkDeployerDir();
		if (sdkDeployerDir==null) {
			throw new IllegalArgumentException("Maven Android SDK deployer directory could not be detected");
		}
		
		
		//ExecutionResult sdkRes= checkSdk(sdkDeployerDir);
		//if (!sdkRes.isSuccess()) {
		//	return sdkRes;
		//}
		String baseDirectory = task.getBaseDirectory();
		logTask("Executing Maven Android in {0} with arguments: {1}...",baseDirectory!=null?baseDirectory:".", getArguments(task));
		
		return executeInDirectory(task);
	}




	protected ExecutionResult executeInDirectory(T task) {
		return super.doExecute(task);
		
	}


	

	
	@Override
	protected void addVmArgs(T task,StringBuilder b) {
		super.addVmArgs(task,b);
		String sdkDir=task.getSdkDir();
		if (sdkDir==null) {
			throw new IllegalArgumentException("Android SDK directory could not be detected");
		}
		b.append(String.format("-Dandroid.sdk.path=\"%s\" ",sdkDir));
		
	}
}
