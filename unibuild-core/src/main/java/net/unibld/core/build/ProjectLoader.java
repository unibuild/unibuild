package net.unibld.core.build;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.unibld.core.config.ProjectConfig;
import net.unibld.core.config.io.IProjectConfigLoadListener;
import net.unibld.core.config.io.ProjectConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Loads a {@link BuildProject} into the memory from the file system
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:31
 */
/**
 * @author andor
 *
 */
@Component
public class ProjectLoader implements IProjectConfigLoadListener {
	private static final Logger LOG=LoggerFactory.getLogger(ProjectLoader.class);
	
	
	@Autowired
	private ProjectConfigurator projectConfigurator;

	
	/**
	 * Loads a project from the specified path. Path should be a valid project 
	 * XML file
	 * @param context Build tool context
	 * @param projectFilePath Path of the project XML file
	 * @param args Program arguments
	 * @return The loaded project configuration
	 * @throws BuildException If any error occurs 
	 */
	public ProjectConfig loadProject(BuildToolContext context,String projectFilePath,String[] args) throws BuildException{
		if (projectFilePath==null) {
			throw new IllegalArgumentException("Project file was null");
		}
		
		File projectFile=new File(projectFilePath);
		if (!projectFile.exists()||!projectFile.isFile()) {
			throw new IllegalArgumentException(BuildConstants.ERR_PROJECT_FILE_DOES_NOT_EXIST+": "+projectFilePath);
		}
		
		String projectPath=new File(projectFile.getAbsolutePath()).getParentFile().getAbsolutePath();
		
		LOG.info("Checking project directory: {}...",projectPath);
		File projectDir=new File(projectPath);
		if (!projectDir.exists()||!projectDir.isDirectory()) {
			throw new IllegalArgumentException("Project dir is not a directory: "+projectPath);
		}

		return readProjectConfig(context,projectFile,args);
		
		
	}

	private ProjectConfig readProjectConfig(BuildToolContext context,File projectFile,String[] args)  {
		
		LOG.debug("Reading project config file: {}...",projectFile.getAbsolutePath());
		if (!projectFile.exists()||!projectFile.isFile()) {
			throw new IllegalArgumentException("Project file not found: "+projectFile.getAbsolutePath());
		}
		
		FileInputStream fis=null;
		
		try {
			fis=new FileInputStream(projectFile);
			projectConfigurator.setExternalArgs(args);
			ProjectConfig ret = projectConfigurator.readConfig(context,ProjectConfig.class,fis);
			LOG.info("Read project config file from: {}...",projectFile.getAbsolutePath());
			
			return ret;
		} catch (Exception ex) {
			LOG.error("Failed to read project config file from: "+projectFile.getAbsolutePath(),ex);
			System.out.println("\nPROJECT LOAD ERROR: "+ex.getMessage());
			ex.printStackTrace();
			throw new BuildException("Failed to read project config file from: "+projectFile.getAbsolutePath(),ex);
		} finally {
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOG.error("Failed to close FileInputStream",e);
				}
			}
		}
	}


	@Override
	public void projectConfigLoaded(ProjectConfig ret) {
		LOG.info("Project loaded: {}",ret.getProjectName());
	}
}//end ProjectLoader