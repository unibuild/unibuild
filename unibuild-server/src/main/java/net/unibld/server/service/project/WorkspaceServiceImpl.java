package net.unibld.server.service.project;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import net.unibld.core.BuildProject;
import net.unibld.core.BuildProjectFactory;
import net.unibld.core.build.BuildToolContext;
import net.unibld.core.build.BuildToolContextHolder;
import net.unibld.core.build.ProjectFileHelper;
import net.unibld.core.build.ProjectLoader;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.config.global.GlobalConfig;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.service.BuildService;
import net.unibld.core.service.ProjectService;
import net.unibld.core.util.PlatformHelper;
import net.unibld.server.service.build.BuildQueue;

@Service("workspaceService")
public class WorkspaceServiceImpl implements WorkspaceService {
	private static final Logger LOG=LoggerFactory.getLogger(WorkspaceServiceImpl.class);
	
	
	@Autowired
	private BuildToolContextHolder contextHolder;
	
	@Autowired
	private BuildQueue buildQueue;
	@Autowired
	private BuildService buildService;
	
	@Autowired
	private ProjectLoader loader;
	@Autowired
	private BuildProjectFactory projectFactory;
	@Autowired
	private ProjectService projectService;

	@Override
	public ProjectConfig loadProjectConfig(Project project) {
		String id=UUID.randomUUID().toString();
		BuildToolContext ctx = contextHolder.createContext(id);
		File projFile=new File(project.getPath());
		
		
		ctx.setBuildDir(ProjectFileHelper.getDefaultBuildDir(projFile.getParentFile().getAbsolutePath()));
		ctx.setProjectDir(projFile.getParentFile().getAbsolutePath());
		
		return loader.loadProject(ctx,project.getPath(),new String[0]);
	}
	
	public void openProject(String path) {
		if (StringUtils.isEmpty(path)) {
			throw new IllegalArgumentException("Project file path not specified");
		}
	
		File projectFile=new File(path);
		if (!projectFile.exists()||!projectFile.isFile()) {
			throw new IllegalArgumentException("Invalid project file path");
		}
		
		String id=UUID.randomUUID().toString();
		BuildToolContext ctx = contextHolder.createContext(id);
		
		File projDir=projectFile.getParentFile();
		
		if (projectService.getProjectByPath(path)!=null) {
			throw new IllegalArgumentException("Project with the same path already exists.");
		}
		
		
		//setup build dir without project name in order to be able to open an initial project
		//build dir will be updated before every run
		ctx.setBuildDir(ProjectFileHelper.getDefaultBuildDir(projDir.getAbsolutePath()));
		ctx.setProjectDir(projDir.getAbsolutePath());
		
		ProjectConfig cfg=loader.loadProject(ctx,path,new String[0]);
		if (projectService.getProjectByName(cfg.getProjectName())!=null) {
			throw new IllegalArgumentException("Project with the same name already exists.");
		}
		
		BuildProject p = projectFactory.createProject(cfg, null);
		
		
		Project ret = projectService.saveProject(p, path, PlatformHelper.getLoggedOnUserName());
		LOG.info("Successfully imported project: {}",path);
	}

	@Override
	public void deleteProject(Project project) {
		if (project==null) {
			throw new IllegalArgumentException("Project was null");
		}
		String currentBuildId = buildQueue.getCurrentBuildId();
		if (currentBuildId!=null) {
			Build build = buildService.findBuild(currentBuildId);
			if (build!=null && build.getProject().getId().equals(project.getId())) {
				throw new IllegalStateException("Build in progress for project");
			}
		}
		
		projectService.deleteProject(project);
		LOG.info("Deleting project: {} [id={}]...",project.getName(),project.getId());
	}
}
