package net.unibld.client.desktop.service;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.unibld.client.desktop.BuildDirHelper;
import net.unibld.core.BuildProject;
import net.unibld.core.BuildProjectFactory;
import net.unibld.core.build.BuildToolContext;
import net.unibld.core.build.BuildToolContextHolder;
import net.unibld.core.build.ProjectFileHelper;
import net.unibld.core.build.ProjectLoader;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.service.BuildService;
import net.unibld.core.service.ProjectService;
import net.unibld.core.util.PlatformHelper;

@Component
public class ClientWorkspaceManager {
	private static final Logger LOG=LoggerFactory.getLogger(ClientWorkspaceManager.class);
	
	@Autowired
	private BuildToolContextHolder contextHolder;
	
	
	@Autowired
	private ProjectService projectService;
	@Autowired
	private BuildService buildService;
	@Autowired
	private ProjectLoader loader;
	@Autowired
	private BuildProjectFactory projectFactory;


	
	
	public ProjectService getProjectService() {
		return projectService;
	}
	
	public Project importProject(String path) {
		String id=UUID.randomUUID().toString();
		BuildToolContext ctx = contextHolder.createContext(id);
		File projectFile=new File(path);
		File projDir=projectFile.getParentFile();
		
		//setup build dir without project name in order to be able to open an initial project
		//build dir will be updated before every run
		ctx.setBuildDir(BuildDirHelper.getBuildPath(""));
		ctx.setProjectDir(projDir.getAbsolutePath());
		
		ProjectConfig cfg=loader.loadProject(ctx,path,new String[0]);
		BuildProject p = projectFactory.createProject(cfg, null);
		
		
		Project ret = projectService.saveProject(p, path, PlatformHelper.getLoggedOnUserName());
		LOG.info("Successfully imported project: {}",path);
		return ret;
	}

	public void deleteAllProjects() {
		buildService.deleteAllBuilds();
		projectService.deleteAllProjects();
		
	}

	public ProjectConfig loadProjectConfig(Project project) {
		String id=UUID.randomUUID().toString();
		BuildToolContext ctx = contextHolder.createContext(id);
		File projFile=new File(project.getPath());
		
		
		ctx.setBuildDir(BuildDirHelper.getBuildPath(project.getName()));
		ctx.setProjectDir(projFile.getParentFile().getAbsolutePath());
		
		return loader.loadProject(ctx,project.getPath(),new String[0]);
	}
}
