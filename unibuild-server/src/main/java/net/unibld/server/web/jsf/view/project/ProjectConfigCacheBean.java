package net.unibld.server.web.jsf.view.project;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Project;
import net.unibld.server.service.project.WorkspaceService;
import net.unibld.server.web.jsf.view.build.BuildTableBean;

@ManagedBean(name="projectConfigCacheBean")
@ViewScoped
public class ProjectConfigCacheBean {
	private static final Logger LOG=LoggerFactory.getLogger(BuildTableBean.class);
	
	@ManagedProperty(value = "#{workspaceService}")
    private WorkspaceService workspaceService;
	
	private Map<String,ProjectConfig> map;
	
	@PostConstruct
	public void init() {
		this.map=new HashMap<>();
	}

	public WorkspaceService getWorkspaceService() {
		return workspaceService;
	}

	public void setWorkspaceService(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}

	public ProjectConfig getProjectConfig(Project project) {
		if (map.containsKey(project.getId())) {
			return map.get(project.getId());
		}
		LOG.info("Loading project config for: {}...",project.getName());
		ProjectConfig c = workspaceService.loadProjectConfig(project);
		map.put(project.getId(), c);
		return c;
	}
	
	
}
