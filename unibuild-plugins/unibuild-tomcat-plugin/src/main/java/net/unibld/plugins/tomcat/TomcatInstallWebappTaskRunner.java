package net.unibld.plugins.tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.unibld.core.ServerDirectory;
import net.unibld.core.build.BuildException;
import net.unibld.core.config.InstanceConfig;
import net.unibld.core.config.ServerConfig;
import net.unibld.core.exec.CmdContext;
import net.unibld.core.script.ScriptManager;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.server.ServerTaskRunner;
import net.unibld.core.util.PlatformHelper;

/**
 * Cleans and installs a Tomcat webapp.
 * @author andor
 *
 */
@Component
public class TomcatInstallWebappTaskRunner extends ServerTaskRunner<TomcatInstallWebappTask>{
	private static final Logger LOG=LoggerFactory.getLogger(TomcatInstallWebappTaskRunner.class);
	
	@Autowired
	private ScriptManager scriptManager;


	protected String getContextNameFromWar(String targetWarName) {
		return targetWarName.replace(".war", "");
	}
	@Override
	protected ExecutionResult execute(TomcatInstallWebappTask task) {
		validate(task);
		
		InstanceConfig instanceConfig = getInstanceConfig(task);
		String serverPath = instanceConfig.getServerConfig().getServerPath();
		String webappPath = FilenameUtils.concat(serverPath, "webapps");
		File webappDir=new File(webappPath);
		
		File warFile=new File(task.getWar());
		if (!warFile.exists()&&!warFile.isFile()) {
			throw new BuildException("WAR file is invalid: "+warFile.getAbsolutePath());
		}
		
		String targetWarName=null;
		if (task.getTargetWarName()==null||task.getTargetWarName().trim().length()==0) {
			targetWarName = warFile.getName();
		} else {
			targetWarName = task.getTargetWarName();
		}
		
		LOG.info("Installing Tomcat webapp {} to {} as: {}...",warFile.getAbsolutePath(), webappDir.getAbsolutePath(), targetWarName);
				
		
		if (PlatformHelper.isLinux()) {
			String script=getLinuxScriptWithArguments(serverPath,warFile,targetWarName,task.isClean());
			LOG.info("Executing deploy script: {}",script);
			CmdContext ctx=new CmdContext();
			String logFilePath = getLogFilePath(task);
			
			try {
				ctx.setLogFilePath(logFilePath);
				ctx.setFailOnError(true);
				ctx.setListener(createListener(task));
				ExecutionResult ret = cmdExecutor.execCmd(ctx,script);
				ret.setLogFilePath(ctx.getLogFilePath());
				return ret;
			} catch (Exception ex) {
				String msg = String.format("Failed to execute process %s",task.getPath());
				logError(msg, ex);
				if (isFailOnError(task)) {
					
					return ExecutionResult.buildError(msg, ex,logFilePath);
				} else {
					return ExecutionResult.buildSuccess();
				}
			}
		} else {
		
			
			try {
				if (task.getTargetWarName()==null||task.getTargetWarName().trim().length()==0) {
					logTask(String.format("Installing WAR file to: %s...",webappDir.getAbsolutePath()));
					

					if (task.isClean()) {
						LOG.info("Cleaning before installing {} in {} (server dir: {})", targetWarName, webappDir.getAbsolutePath(), serverPath);
						clean(instanceConfig, webappDir, targetWarName);
					}
					
					FileUtils.copyFileToDirectory(warFile, webappDir);
				} else {
					File target = new File(FilenameUtils.concat(webappPath, task.getTargetWarName()));
					logTask(String.format("Installing WAR file to: %s...",target.getAbsolutePath()));
					

					if (task.isClean()) {
						LOG.info("Cleaning before installing {} in {} (server dir: {})", targetWarName, webappDir.getAbsolutePath(), serverPath);
						clean(instanceConfig, webappDir, targetWarName);
					}
					
					FileUtils.copyFile(warFile, target);
				}
				return ExecutionResult.buildSuccess();
			} catch (IOException e) {
				String msg = String.format("Failed to copy WAR file %s to: %s",warFile.getAbsolutePath(),webappDir.getAbsolutePath());
				LOG.error(msg,e);
				return ExecutionResult.buildError(msg,e);
			}
		}
	}
	
	private String getLinuxScriptWithArguments(String serverPath, File warFileToInstall, String targetWarName, boolean clean) {
		InputStream is = getClass().getResourceAsStream("/scripts/tomcat-deploy.sh");
		if (is==null) {
			throw new IllegalStateException("Script not found: tomcat-deploy.sh");
		}
		File script = scriptManager.installScriptIfNecessary(is, "tomcat", "tomcat-deploy.sh");
		return String.format("sh %s %s %s %s %s", script.getAbsolutePath(), 
				serverPath, warFileToInstall, getContextNameFromWar(targetWarName), clean?"true":"false");
	}
	protected void clean(InstanceConfig cfg, File webappDir, String targetWarName) {
		//cleaning webapps folder
		String[] list = webappDir.list();
		
		if (list!=null) {
			String contextName = getContextNameFromWar(targetWarName);
			for (String file:list) {
				String path=FilenameUtils.concat(webappDir.getAbsolutePath(), file);
				File f=new File(path);
				if (f.isDirectory()) {
					if (file.equals(contextName)) {
						try {
							FileUtils.deleteDirectory(f);
							LOG.info("Deleted context directory: {}",f.getAbsolutePath());
						} catch (Exception ex) {
							LOG.error("Failed to delete context directory: "+f.getAbsolutePath(),ex);
						}
					}
				} else {
					if (file.equals(contextName+".war")) {
						try {
							Files.delete(f.toPath());
						} catch (IOException ex) {
							LOG.error("Failed to delete context WAR file: "+f.getAbsolutePath(),ex);
						}
						LOG.info("Deleted context WAR file: {}",f.getAbsolutePath());
					}
				}
			}
		}
		
		//cleaning other externally specified folders
		ServerConfig sc = cfg.getServerConfig();
		List<ServerDirectory> dirsToClean = sc.getDirsToClean();
		if (dirsToClean!=null) {
			for (ServerDirectory sd:dirsToClean) {
				cleanDirContent(new File(FilenameUtils.concat(sc.getServerPath(),sd.getPath())));
			}
		}
	}

	protected void cleanDirContent(File dir) {
		String[] list = dir.list();
		for (String file:list) {
			String path=FilenameUtils.concat(dir.getAbsolutePath(), file);
			File f=new File(path);
			if (f.isDirectory()) {
				try {
					FileUtils.deleteDirectory(f);
					LOG.info("Deleted directory: {}",f.getAbsolutePath());
				} catch (Exception ex) {
					LOG.error("Failed to delete directory: "+f.getAbsolutePath(),ex);
				}
			} else {
				try {
					Files.delete(f.toPath());
				} catch (IOException ex) {
					LOG.error("Failed to delete file: "+f.getAbsolutePath(),ex);
				}
				LOG.info("Deleted file: {}",f.getAbsolutePath());
			}
		}
		
		LOG.info("Cleaned dir content: {}",dir.getAbsolutePath());
	}
	
	protected void cleanAppContent(File dir, String warFileName) {
		String[] list = dir.list();
		
		for (String file:list) {
			String path=FilenameUtils.concat(dir.getAbsolutePath(), file);
			File f=new File(path);
			if (f.isDirectory()&&f.getName().equals(FilenameUtils.getBaseName(warFileName))) {
				try {
					FileUtils.deleteDirectory(f);
					LOG.info("Deleted directory: {}",f.getAbsolutePath());
				} catch (Exception ex) {
					LOG.error("Failed to delete directory: "+f.getAbsolutePath(),ex);
				}
			} else if (f.isFile()&&f.getName().equals(warFileName)) {
				f.delete();
				LOG.info("Deleted file: {}",f.getAbsolutePath());
			}
		}
		
		LOG.info("Cleaned app content ({}) in: {}",warFileName,dir.getAbsolutePath());
	}

	protected void validate(TomcatInstallWebappTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		
		
		if (task.getWar()==null) {
			throw new IllegalStateException("Task WAR path was null");
		}

	}


	@Override
	protected String getTaskName() {
		return "tomcat-install-webapp";
	}
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
