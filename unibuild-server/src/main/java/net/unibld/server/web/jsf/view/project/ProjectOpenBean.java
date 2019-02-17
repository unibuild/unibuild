package net.unibld.server.web.jsf.view.project;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Project;
import net.unibld.server.service.project.WorkspaceService;
import net.unibld.server.web.jsf.FacesMessageHelper;

/**
 * Managed bean to open a {@link Project} configuration
 * @author andor
 * @see Project
 * @see ProjectConfig
 *
 */
@ManagedBean(name="projectOpenBean")
@ViewScoped
public class ProjectOpenBean implements Serializable {

	private static final long serialVersionUID = 6159825183310583698L;

	private static final Logger LOG=LoggerFactory.getLogger(ProjectOpenBean.class);
	
	private String path;
	

	@ManagedProperty(value = "#{workspaceService}")
    private WorkspaceService workspaceService;
	


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void openProject() {
		LOG.info("Opening server project...");
		try {
			workspaceService.openProject(path);
			RequestContext.getCurrentInstance().execute("PF('projectOpenDialog').hide();");
		} catch (Exception ex) {
			LOG.error("Failed to open project: "+path,ex);
			FacesMessageHelper.addError(ex.getMessage());
		}
	}

	public WorkspaceService getWorkspaceService() {
		return workspaceService;
	}

	public void setWorkspaceService(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}

	
}
