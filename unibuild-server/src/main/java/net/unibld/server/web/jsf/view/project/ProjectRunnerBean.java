package net.unibld.server.web.jsf.view.project;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.ProjectUserRight;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.service.build.BuildQueue;
import net.unibld.server.service.build.BuildQueueRequest;
import net.unibld.server.service.build.BuildStateManager;
import net.unibld.server.service.project.WorkspaceService;
import net.unibld.server.service.security.ProjectSecurityService;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.web.jsf.DatatableLogger;
import net.unibld.server.web.jsf.FacesMessageHelper;
import net.unibld.server.web.jsf.view.build.BuildTableBean;
import net.unibld.server.web.jsf.view.build.PasswordReaderBean;

/**
 * Managed bean to run a project goal
 * @author andor
 *
 */
@ManagedBean(name="projectRunnerBean")
@ViewScoped
public class ProjectRunnerBean {
	private static final Logger LOG=LoggerFactory.getLogger(ProjectRunnerBean.class);
	
	@ManagedProperty(value = "#{projectTableBean}")
    private ProjectTableBean projectTableBean;
	
	@ManagedProperty(value = "#{buildTableBean}")
    private BuildTableBean buildTableBean;
	
	@ManagedProperty(value = "#{passwordReaderBean}")
    private PasswordReaderBean passwordReaderBean;
	
	
	@ManagedProperty(value = "#{buildQueue}")
    private BuildQueue buildQueue;
	
	@ManagedProperty(value = "#{buildStateManager}")
    private BuildStateManager buildStateManager;
	
	@ManagedProperty(value = "#{projectConfigCacheBean}")
    private ProjectConfigCacheBean projectConfigCacheBean;
	
	@ManagedProperty(value = "#{projectSecurityService}")
    private ProjectSecurityService projectSecurityService;
	
	@ManagedProperty(value = "#{securityService}")
    private SecurityService securityService;
	
	
	
	private String selectedGoal;

	
	public Project getSelectedProject() {
		return projectTableBean.getSelectedItem();
	}


	public String getSelectedGoal() {
		return selectedGoal;
	}
	
	public boolean isSelectedGoalConfirmed() {
		if (selectedGoal==null) {
			return false;
		}
		
		if (projectTableBean.getSelectedItem()==null) {
			return false;
		}
		
		ProjectConfig conf = projectConfigCacheBean.getProjectConfig(projectTableBean.getSelectedItem());
		List<BuildGoalConfig> goals = conf.getGoalsConfig().getGoals();
		if (goals!=null) {
			for (BuildGoalConfig goal:goals) {
				if (goal.getName().equals(selectedGoal)) {
					return goal.isConfirm();
				}
			}
		}
		throw new IllegalStateException("Goal not found: "+selectedGoal);
	}

	public void setSelectedGoal(String selectedGoal) {
		this.selectedGoal = selectedGoal;
	}
	
	public void runGoal() {
		if (StringUtils.isEmpty(selectedGoal)) {
			FacesMessageHelper.addError("No goal selected");
			return;
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth==null) {
			throw new IllegalStateException("Not authenticated");
		}
		
		try {
			BuildQueueRequest req=new BuildQueueRequest().withProject(getSelectedProject()).
					withGoal(selectedGoal).
					withListener(buildStateManager).
					withExternalLogger(new DatatableLogger(buildStateManager)).
					withLogConsumer(buildStateManager).
					withPasswordReader(passwordReaderBean).
					withUserId(auth.getName());
			boolean started=buildQueue.addToQueue(req);
			if (started) {
				FacesMessageHelper.addInfo(String.format("Goal %s started for: %s",selectedGoal,getSelectedProject().getName()));
			} else {
				FacesMessageHelper.addInfo(String.format("Goal %s queued for: %s",selectedGoal,getSelectedProject().getName()));
			}
		} catch (Exception ex) {
			LOG.error("Failed to start build",ex);
			FacesMessageHelper.addError(ex.getMessage());
		}
	}

	public void rerunBuild() {
		Build build = buildTableBean.getSelectedItem();
		if (build==null) {
			FacesMessageHelper.addError("No build selected");
			return;
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth==null) {
			throw new IllegalStateException("Not authenticated");
		}
		
		try {
			BuildQueueRequest req=new BuildQueueRequest().withProject(build.getProject()).
					withGoal(build.getGoal()).
					withListener(buildStateManager).
					withExternalLogger(new DatatableLogger(buildStateManager)).
					withLogConsumer(buildStateManager).
					withPasswordReader(passwordReaderBean).
					withUserId(auth.getName());
			boolean started=buildQueue.addToQueue(req);
			if (started) {
				FacesMessageHelper.addInfo(String.format("Goal %s restarted for: %s",build.getGoal(),build.getProject().getName()));
			} else {
				FacesMessageHelper.addInfo(String.format("Goal %s queued again for: %s",build.getGoal(),build.getProject().getName()));
			}
		} catch (Exception ex) {
			LOG.error("Failed to restart build",ex);
			FacesMessageHelper.addError(ex.getMessage());
		}
		
	}
	
	
	
	public List<SelectItem> getGoalItems() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth==null) {
			return null;
		}
		UserProfile user = securityService.findUserProfile(auth.getName());
		if (user==null) {
			return null;
		}
		if (projectTableBean.getSelectedItem()==null) {
			return null;
		}
		
		ProjectConfig selectedProjectConfig=projectConfigCacheBean.getProjectConfig(projectTableBean.getSelectedItem());
		
		
		
		boolean admin = securityService.hasRole(user.getUserName(), Group.ROLE_ADMIN);
		
		ProjectUserRight right = projectSecurityService.getUserProjectRights(user, projectTableBean.getSelectedItem());
		
		List<SelectItem> ret=new ArrayList<>();
		List<BuildGoalConfig> goals = selectedProjectConfig.getGoalsConfig().getGoals();
		if (goals!=null) {
			for (BuildGoalConfig goal:goals) {
				if (goal.getAccessLevel()==null || admin || 
						projectSecurityService.isAccessLevelHigherOrEqual(right.getAccessLevel(),goal.getAccessLevel())) {
				
					ret.add(new SelectItem(goal.getName(),goal.getName()));
				}
			}
		}
		return ret;
	}

	public ProjectTableBean getProjectTableBean() {
		return projectTableBean;
	}

	public void setProjectTableBean(ProjectTableBean projectTableBean) {
		this.projectTableBean = projectTableBean;
	}


	public BuildQueue getBuildQueue() {
		return buildQueue;
	}


	public void setBuildQueue(BuildQueue buildQueue) {
		this.buildQueue = buildQueue;
	}



	public BuildTableBean getBuildTableBean() {
		return buildTableBean;
	}


	public void setBuildTableBean(BuildTableBean buildTableBean) {
		this.buildTableBean = buildTableBean;
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


	public BuildStateManager getBuildStateManager() {
		return buildStateManager;
	}


	public void setBuildStateManager(BuildStateManager buildStateManager) {
		this.buildStateManager = buildStateManager;
	}


	public ProjectConfigCacheBean getProjectConfigCacheBean() {
		return projectConfigCacheBean;
	}


	public void setProjectConfigCacheBean(ProjectConfigCacheBean projectConfigCacheBean) {
		this.projectConfigCacheBean = projectConfigCacheBean;
	}


	public PasswordReaderBean getPasswordReaderBean() {
		return passwordReaderBean;
	}


	public void setPasswordReaderBean(PasswordReaderBean passwordReaderBean) {
		this.passwordReaderBean = passwordReaderBean;
	}
}
