package net.unibld.server.web.jsf.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.persistence.model.Project;
import net.unibld.core.persistence.model.ProjectAccessLevel;
import net.unibld.core.service.ProjectService;
import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.ProjectUserRight;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.UserStatus;
import net.unibld.server.service.security.ProjectSecurityService;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.service.security.UserItem;
import net.unibld.server.service.security.UserListService;
import net.unibld.server.service.security.ticket.UserTicketService;
import net.unibld.server.web.jsf.FacesMessageHelper;
import net.unibld.server.web.jsf.LocalizationHelper;
import net.unibld.server.web.jsf.datatable.ITableDataProvider;
import net.unibld.server.web.jsf.datatable.UserListProvider;


/**
 * Managed bean to list users for administration.
 * @author andor
 *
 */
@ManagedBean(name="userTableBean")
@ViewScoped
public class UserTableBean extends AbstractModelLazyTableBean<UserItem> {
	private static final Logger LOG=LoggerFactory.getLogger(UserTableBean.class);
	
	@ManagedProperty(value = "#{userListService}")
    private UserListService userListService;
	
	@ManagedProperty(value = "#{securityService}")
    private SecurityService securityService;
	
	@ManagedProperty(value = "#{projectService}")
    private ProjectService projectService;
	
	@ManagedProperty(value = "#{projectSecurityService}")
    private ProjectSecurityService projectSecurityService;
	
	@ManagedProperty(value = "#{userTicketService}")
    private UserTicketService userTicketService;
	
	@Override
	protected ITableDataProvider<UserItem> createDataProvider() {
		return new UserListProvider(userListService,userTicketService,this);
	}


	private UserItem selectedUser;
	
	private UserStatus statusFilter;
	private String groupFilter;

	private List<ProjectUserRight> projectRights;
	

	public UserItem getNewEntity() {
		UserItem ret = new UserItem();
		ret.setProfile(new UserProfile());
		ret.setAuthority(new Authority());
		return ret;
	}
	
	
	public String banUser() {
		if (selectedUser==null) {
			throw new IllegalStateException("Selected user was null");
		}
		
		securityService.banUser(selectedUser.getProfile());
		return "/admin/users?faces-redirect=true";
		
	}
	public String unbanUser() {
		if (selectedUser==null) {
			throw new IllegalStateException("Selected user was null");
		}
		
		securityService.unbanUser(selectedUser.getProfile());
		return "/admin/users?faces-redirect=true";
		
	}

	public void saveUser() {
		if (selectedUser==null) {
			throw new IllegalStateException("Selected user was null");
		}
		if (selectedUser.getProfile()==null) {
			throw new IllegalStateException("User profile was null");
		}
		LOG.info("Saving user: {}...",selectedUser.getProfile().getUserName());
		
		try {
			securityService.saveUser(selectedUser.getProfile(),selectedUser.getGroup(),null);
			FacesMessageHelper.addInfo("User saved successfully: "+selectedUser.getProfile().getUserName());
			RequestContext.getCurrentInstance().execute("PF('userDialog').hide()");
			selectedUser=null;
		} catch (Exception e) {
			LOG.error("Failed to save user",e);
			FacesMessageHelper.addError("User save failed: "+e.getMessage());
		}
		
		
	}
	
	public List<SelectItem> getAccessLevelItems() {
		return Stream.of(ProjectAccessLevel.values())
	        .map(s -> new SelectItem(s,s.name()))
	        .collect(Collectors.toList());
	}
	
	public String getEditTitle() {
		if (selectedUser==null||selectedUser.getProfile().getRegisterDate()==null) {
			return "New user";
		} else {
			return String.format("Edit user [id=%s]",selectedUser.getProfile().getUserName());
		}
	}

	
	public List<SelectItem> getStatusItems() {
		
		
		List<SelectItem> ret=new ArrayList<SelectItem>();
		for (UserStatus s:UserStatus.values()) {
			ret.add(new SelectItem(s, LocalizationHelper.getLocalizedMessage("msg", UserStatus.class.getName()+"@"+s.name())));
		}
		return ret;
	}
	
	public List<SelectItem> getGroupItems() {
		List<SelectItem> ret=new ArrayList<SelectItem>();
		for (Group g:Group.values()) {
			ret.add(new SelectItem(g.name(), LocalizationHelper.getLocalizedMessage("msg", Group.class.getName()+"@"+g.name())));
		}
		return ret;
	}

	public UserItem getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(UserItem selectedUser) {
		this.selectedUser = selectedUser;
		if (this.selectedUser!=null) {
			this.projectRights = loadProjectRights();
			
		} else {
			this.projectRights=null;
		}
	}

	private List<ProjectUserRight> loadProjectRights() {
		List<ProjectUserRight> rights = projectSecurityService.getUserRights(selectedUser.getProfile());
		Map<String, ProjectUserRight> map = rights.stream().collect(
                Collectors.toMap(ProjectUserRight::getProjectId, c->c));

		List<ProjectUserRight> ret=new ArrayList<>();
		
		List<Project> projects=projectService.getAvailableProjectsOrderByName();
		for (Project p:projects) {
			if (map.containsKey(p.getId())) {
				ret.add(map.get(p.getId()));
			} else {
				ProjectUserRight r=new ProjectUserRight();
				r.setProject(p);
				r.setAccessLevel(ProjectAccessLevel.none);
				r.setUser(selectedUser.getProfile());
				ret.add(r);
			}
		}
		return ret;
	}


	public void saveUserRights() {
		if (selectedUser==null) {
			throw new IllegalStateException("Selected user was null");
		}
		if (projectRights==null) {
			throw new IllegalStateException("Project rights list list was null");
		}
		LOG.info("Saving user rights: {}...",selectedUser.getProfile().getUserName());
		
		try {
			projectSecurityService.saveUserRights(selectedUser.getProfile(),projectRights);
			FacesMessageHelper.addInfo("User project rights saved successfully: "+selectedUser.getProfile().getUserName());
			RequestContext.getCurrentInstance().execute("PF('userRightsDialog').hide()");
			selectedUser=null;
		} catch (Exception e) {
			LOG.error("Failed to save user rights",e);
			FacesMessageHelper.addError("User rights save failed: "+e.getMessage());
		}
		
		
	}
	public void generateUserTicket() {
		if (selectedUser==null) {
			throw new IllegalStateException("Selected user was null");
		}
		if (selectedUser.getAccessTicket()!=null) {
			throw new IllegalStateException("Access ticket already exists");
		}
		LOG.info("Generating access ticket: {}...",selectedUser.getProfile().getUserName());
		
		try {
			selectedUser.setAccessTicket(userTicketService.createAccessTicket(selectedUser.getProfile().getUserName()));
			FacesMessageHelper.addInfo("User access ticket generated successfully: "+selectedUser.getProfile().getUserName());
		} catch (Exception e) {
			LOG.error("Failed to generate user ticket",e);
			FacesMessageHelper.addError("User ticket generation failed: "+e.getMessage());
		}
		
		
	}
	
	public void regenerateUserTicket() {
		if (selectedUser==null) {
			throw new IllegalStateException("Selected user was null");
		}
		if (selectedUser.getAccessTicket()==null) {
			throw new IllegalStateException("Access ticket does not exist");
		}
		LOG.info("Regenerating access ticket: {}...",selectedUser.getProfile().getUserName());
		
		try {
			selectedUser.setAccessTicket(userTicketService.createAccessTicket(selectedUser.getProfile().getUserName()));
			FacesMessageHelper.addInfo("User access ticket regenerated successfully: "+selectedUser.getProfile().getUserName());
		} catch (Exception e) {
			LOG.error("Failed to regenerate user ticket",e);
			FacesMessageHelper.addError("User ticket regeneration failed: "+e.getMessage());
		}
		
		
	}
	
	public void deleteUserTicket() {
		if (selectedUser==null) {
			throw new IllegalStateException("Selected user was null");
		}
		if (selectedUser.getAccessTicket()!=null) {
			throw new IllegalStateException("Access ticket already exists");
		}
		LOG.info("Deleting access ticket: {}...",selectedUser.getProfile().getUserName());
		
		try {
			userTicketService.deleteAccessTicket(selectedUser.getProfile().getUserName());
			selectedUser.setAccessTicket(null);
			FacesMessageHelper.addInfo("User access ticket deleted successfully: "+selectedUser.getProfile().getUserName());
		} catch (Exception e) {
			LOG.error("Failed to delete user ticket",e);
			FacesMessageHelper.addError("User ticket delete failed: "+e.getMessage());
		}
		
		
	}
	
	

	public UserStatus getStatusFilter() {
		return statusFilter;
	}

	public void setStatusFilter(UserStatus statusFilter) {
		this.statusFilter = statusFilter;
	}

	public UserListService getUserListService() {
		return userListService;
	}

	public void setUserListService(UserListService userListService) {
		this.userListService = userListService;
	}

	public String getGroupFilter() {
		return groupFilter;
	}

	public void setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
	}

	public SecurityService getSecurityService() {
		return securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	



	public ProjectSecurityService getProjectSecurityService() {
		return projectSecurityService;
	}


	public void setProjectSecurityService(
			ProjectSecurityService projectSecurityService) {
		this.projectSecurityService = projectSecurityService;
	}


	public UserTicketService getUserTicketService() {
		return userTicketService;
	}


	public void setUserTicketService(UserTicketService userTicketService) {
		this.userTicketService = userTicketService;
	}


	public ProjectService getProjectService() {
		return projectService;
	}


	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}


	public List<ProjectUserRight> getProjectRights() {
		return projectRights;
	}


	public void setProjectRights(List<ProjectUserRight> projectRights) {
		this.projectRights = projectRights;
	}
	
}
