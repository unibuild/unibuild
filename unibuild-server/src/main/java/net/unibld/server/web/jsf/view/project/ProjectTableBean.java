package net.unibld.server.web.jsf.view.project;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import net.unibld.core.persistence.model.Project;
import net.unibld.core.persistence.model.ProjectAccessLevel;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.ProjectUserRight;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.service.project.ProjectListService;
import net.unibld.server.service.project.WorkspaceService;
import net.unibld.server.service.security.ProjectSecurityService;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.web.jsf.FacesMessageHelper;
import net.unibld.server.web.jsf.datatable.ITableDataProvider;
import net.unibld.server.web.jsf.datatable.project.ProjectListProvider;
import net.unibld.server.web.jsf.view.AbstractModelLazyTableBean;

/**
 * JSF managed bean to list and select available {@link Project} records
 * @author andor
 *
 */
@ManagedBean(name="projectTableBean")
@ViewScoped
public class ProjectTableBean extends AbstractModelLazyTableBean<Project> {
	private static final Logger LOG=LoggerFactory.getLogger(ProjectTableBean.class);
	
	@ManagedProperty(value = "#{projectListService}")
    private ProjectListService projectListService;
	
	@ManagedProperty(value = "#{workspaceService}")
    private WorkspaceService workspaceService;
	
	@ManagedProperty(value = "#{projectSecurityService}")
    private ProjectSecurityService projectSecurityService;
	
	@ManagedProperty(value = "#{securityService}")
    private SecurityService securityService;
	

	@Override
	protected ITableDataProvider<Project> createDataProvider() {
		return new ProjectListProvider(projectListService);
	}


	/**
	 * Tries to delete a {@link Project} record
	 */
	public void deleteProject() {
		if (selectedItem==null) {
			throw new IllegalStateException("Selected project");
		}
		LOG.info("Deleting project: {} [id={}]...",selectedItem.getName(),selectedItem.getId());
		try {
			workspaceService.deleteProject(selectedItem);
			LOG.info("Deleted project: {} [id={}]",selectedItem.getName(),selectedItem.getId());
			FacesMessageHelper.addInfo("Project deleted: "+selectedItem.getName());
		} catch (Exception ex) {
			LOG.error("Failed to delete project: "+selectedItem.getName(),ex);
			FacesMessageHelper.addError(ex.getMessage());
		}
	}

	public boolean hasAccessToProject(Project project) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth==null) {
			return false;
		}
		UserProfile user = securityService.findUserProfile(auth.getName());
		if (user==null) {
			return false;
		}
		if (securityService.hasRole(user.getUserName(), Group.ROLE_ADMIN)) {
			return true;
		}
		ProjectUserRight r = projectSecurityService.getUserProjectRights(user, project);
		return r!=null && r.getAccessLevel()!=ProjectAccessLevel.none;
	}


	public ProjectListService getProjectListService() {
		return projectListService;
	}



	public void setProjectListService(ProjectListService projectListService) {
		this.projectListService = projectListService;
	}



	public WorkspaceService getWorkspaceService() {
		return workspaceService;
	}



	public void setWorkspaceService(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}



	public ProjectSecurityService getProjectSecurityService() {
		return projectSecurityService;
	}



	public void setProjectSecurityService(ProjectSecurityService projectSecurityService) {
		this.projectSecurityService = projectSecurityService;
	}



	public SecurityService getSecurityService() {
		return securityService;
	}



	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
}
