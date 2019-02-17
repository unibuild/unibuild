package net.unibld.plugins.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.exec.CmdContext;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.java.AbstractSignJarTaskRunner;
import net.unibld.core.util.Zip;


/**
 * Executes a jarsigner command to sign an Android apk using:<br>
 * jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.keystore
 *		my_application.apk alias_name<br>
 * The task runner is also capable of "zipalign"-ing the signed APK, using the zipalign tool bundled
 * in Android SDK:<br>
 * zipalign -v 4 your_project_name-unaligned.apk your_project_name.apk
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:28
 */
@Component
public class AndroidSignApkTaskRunner extends AbstractSignJarTaskRunner<AndroidSignApkTask> {
	private static final Logger LOG=LoggerFactory.getLogger(AndroidSignApkTaskRunner.class);
	
	
	@Override
	protected String getTaskName() {
		return "android-signapk";
	}




	@Override
	protected String getStorePassword(AndroidSignApkTask task) {
		if (task.getAntProperties()!=null) {
			return getAntProperty(task,"key.store.password");
		} else {
			return super.getStorePassword(task);
		}
	}








	protected String getAntProperty(AndroidSignApkTask task,String key) {
		LOG.info("Getting property {} from {}...",key,task.getAntProperties());
		File antPropsFile=new File(task.getAntProperties());
		if (!antPropsFile.exists()) {
			throw new BuildException("ant.properties file does not exist: "+task.getAntProperties());
		}
		InputStream is=null;

		try {
			is=new FileInputStream(task.getAntProperties());
			Properties p=new Properties();
			p.load(is);
			return p.getProperty(key);
		} catch (Exception e) {
			LOG.error("File not found: "+task.getAntProperties(),e);
			throw new BuildException("ant.properties file does not exist: "+task.getAntProperties());
		} finally {
			if (is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					LOG.error("Failed to close stream",e);
				}
			}
		}
	}








	@Override
	protected String getKeyPassword(AndroidSignApkTask task) {
		if (task.getAntProperties()!=null) {
			return getAntProperty(task,"key.alias.password");
		} else {
			return super.getStorePassword(task);
		}
	}








	@Override
	protected String getAlias(AndroidSignApkTask task) {
		if (task.getAntProperties()!=null) {
			return getAntProperty(task, "key.alias");
		}
		return super.getAlias(task);
	}








	@Override
	protected boolean isAliasSpecified(AndroidSignApkTask task) {
		return true;
	}








	@Override
	protected String getKeystore(AndroidSignApkTask task) {
		if (task.getAntProperties()!=null) {
			return getAntProperty(task, "key.store");
		}
		return super.getKeystore(task);
	}








	@Override
	protected ExecutionResult execute(AndroidSignApkTask task) {
		
		LOG.info("Executing Android sign APK task with input: {} ...",task.getInput());
		String exe = getExecutableFile(task);
		if (task.isAlreadySigned()) {
			extractSignedApk(task);
			logTask(String.format("Signing already signed APK: %s...", task.getInput()));
			
		} else {
			logTask(String.format("Signing APK %s", task.getInput()));
		}
		task.setPath(exe);
		
		
		String origOutput=task.getOutput();
		if (task.isZipAlign()) {
			task.setOutput(task.getOutput().replace(".apk", "-unaligned.apk"));
		}
		
		ExecutionResult res = execCmd(task);
		if (res.isSuccess()) {
			String sdkDir=task.getSdkDir();
			if (sdkDir==null) {
				throw new BuildException("SDK dir not defined");
			}
			if (sdkDir!=null) {
				LOG.info("Using Android SDK for zipalign: {}...",sdkDir);
				
				CmdContext ctx=new CmdContext();
				ctx.setFailOnError(true);
				ctx.setListener(createListener(task));
				ctx.setEnv(getEnvironment(task));
				
				String cmd=String.format("%s/tools/zipalign -v 4 %s %s",sdkDir,task.getOutput(),origOutput);
				try {
					res=cmdExecutor.execCmd(ctx,cmd);
				} catch (Exception ex) {
					String msg = String.format("Failed to execute zipalign: %s",task.getPath());
					logError(msg, ex);
					res=ExecutionResult.buildError(msg, ex);
					
				}
			} else {
				throw new BuildException("Android SDK not found");
			}
		}
		return res;
	}








	/**
	 * Extracts a signed and zipped APK file to the relative directory 'extract'
	 */
	public void extractSignedApk(AndroidSignApkTask task) {
		if (task.getInput()==null) {
			throw new BuildException("Input APK not specified");
		}
		File inputFile=new File(task.getInput());
		if (!inputFile.exists()||!inputFile.isFile()) {
			throw new BuildException("Invalid input APK: "+task.getInput());
		}
		File zipFile=new File(FilenameUtils.concat(inputFile.getParentFile().getAbsolutePath(),inputFile.getName()+".zip"));
		LOG.info("Copying signed APK {} to: {}...",inputFile.getAbsolutePath(),zipFile.getAbsolutePath());
		try {
			FileUtils.copyFile(inputFile, zipFile);
		} catch (IOException e) {
			LOG.error("Failed to copy signed APK to: "+zipFile.getAbsolutePath(),e);
			throw new BuildException("Failed to copy signed APK to: "+zipFile.getAbsolutePath(),e);
		}
		
		String extractPath = FilenameUtils.concat(inputFile.getParentFile().getAbsolutePath(), "extract");
		File extractDir=new File(extractPath);
		extractDir.mkdirs();
		
		LOG.info("Unzipping signed APK {} to: {}...",zipFile.getAbsolutePath(),extractDir.getAbsolutePath());
		try {
			Zip.unzip(zipFile.getAbsolutePath(), extractDir.getAbsolutePath());
		} catch (Exception e) {
			LOG.error("Failed to unzip signed APK to: "+extractDir.getAbsolutePath(),e);
			throw new BuildException("Failed to unzip signed APK to: "+extractDir.getAbsolutePath(),e);
		}
		
		File metaInfDir=new File(FilenameUtils.concat(extractPath,"META-INF"));
		if (metaInfDir.exists()&&metaInfDir.isDirectory()) {
			LOG.info("Removing META-INF from  signed APK extract: {}...",metaInfDir.getAbsolutePath());
			try {
				FileUtils.deleteDirectory(metaInfDir);
			} catch (IOException e) {
				LOG.error("Failed to remove META-INF dir: "+metaInfDir.getAbsolutePath(),e);
				throw new BuildException("Failed to remove META-INF dir: "+metaInfDir.getAbsolutePath(),e);
			}
		} else {
			throw new BuildException("META-INF dir not found: "+metaInfDir.getAbsolutePath());
		}
		String unsignedZipPath = FilenameUtils.concat(inputFile.getParentFile().getAbsolutePath(), inputFile.getName().replace(".apk", "-unsigned.apk"));
		
		LOG.info("Zipping extracted APK {} to: {}...",extractDir.getAbsolutePath(),unsignedZipPath);
		try {
			Zip.zip(extractDir.getAbsolutePath(), unsignedZipPath);
		} catch (Exception e) {
			LOG.error("Failed to zip extracted APK to: "+unsignedZipPath,e);
			throw new BuildException("Failed to zip extracted APK to: "+unsignedZipPath,e);
		}
	
		task.setInput(unsignedZipPath);
	}

}//end AndroidSignApkTaskRunner