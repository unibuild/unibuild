package net.unibld.plugins.tomcat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.ServerDirectory;
import net.unibld.core.build.BuildException;
import net.unibld.core.config.ServerConfig;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.server.ServerTaskRunner;

/**
 * Cleans and installs a Tomcat webapp.
 * @author andor
 *
 */
@Component
public class TomcatInstallWebappTaskRunner extends ServerTaskRunner<TomcatInstallWebappTask>{
	private static final Logger LOG=LoggerFactory.getLogger(TomcatInstallWebappTaskRunner.class);
	
	private File webappDir;

	private File warFile;



	protected String getContextNameFromWar(TomcatInstallWebappTask task) {
		if (task.getTarget()==null||task.getTarget().trim().length()==0) {
			return warFile.getName().replace(".war", "");
		} else {
			return task.getTarget().replace(".war", "");
		}
	}
	@Override
	protected ExecutionResult execute(TomcatInstallWebappTask task) {
		validate(task);
		
		String serverPath = getInstanceConfig(task).getServerConfig().getServerPath();
		String webappPath = FilenameUtils.concat(serverPath, "webapps");
		webappDir=new File(webappPath);
		
		warFile=new File(task.getWar());
		if (!warFile.exists()&&!warFile.isFile()) {
			throw new BuildException("WAR file is invalid: "+warFile.getAbsolutePath());
		}
		
		
		if (task.isClean()) {
			clean(task);
		}
		
		
		try {
			if (task.getTarget()==null||task.getTarget().trim().length()==0) {
				logTask(String.format("Installing WAR file to: %s...",webappDir.getAbsolutePath()));
				FileUtils.copyFileToDirectory(warFile, webappDir);
			} else {
				File target = new File(FilenameUtils.concat(webappPath, task.getTarget()));
				logTask(String.format("Installing WAR file to: %s...",target.getAbsolutePath()));
				FileUtils.copyFile(warFile, target);
			}
			return ExecutionResult.buildSuccess();
		} catch (IOException e) {
			String msg = String.format("Failed to copy WAR file %s to: %s",warFile.getAbsolutePath(),webappDir.getAbsolutePath());
			LOG.error(msg,e);
			return ExecutionResult.buildError(msg,e);
		}
	}
	
	protected void clean(TomcatInstallWebappTask task) {
		//cleaning webapps folder
		String[] list = webappDir.list();
		
		if (list!=null) {
			String contextName = getContextNameFromWar(task);
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
						f.delete();
						LOG.info("Deleted context WAR file: {}",f.getAbsolutePath());
					}
				}
			}
		}
		
		//cleaning other externally specified folders
		ServerConfig sc = getInstanceConfig(task).getServerConfig();
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
				f.delete();
				LOG.info("Deleted file: {}",f.getAbsolutePath());
			}
		}
		
		LOG.info("Cleaned dir content: {}",dir.getAbsolutePath());
	}
	
	protected void cleanAppContent(File dir) {
		String[] list = dir.list();
		String warFileName=warFile.getName();
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
