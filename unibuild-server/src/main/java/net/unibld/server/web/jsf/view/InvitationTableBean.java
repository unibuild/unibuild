package net.unibld.server.web.jsf.view;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.entities.security.invitation.InvitationStatus;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.service.security.SpringSecurityException;
import net.unibld.server.service.security.invitation.InvitationListService;
import net.unibld.server.service.security.invitation.InvitationService;
import net.unibld.server.web.jsf.LocalizationHelper;
import net.unibld.server.web.jsf.datatable.ITableDataProvider;
import net.unibld.server.web.jsf.datatable.InvitationListProvider;



/**
 * A JSF bean to invite new users as an administrator and list invitations already sent.
 * @author andor
 *
 */
@ManagedBean(name="invitationTableBean")
@ViewScoped
public class InvitationTableBean extends AbstractModelLazyTableBean<Invitation> {
	
	@ManagedProperty(value = "#{invitationService}")
    private InvitationService invitationService;
	@ManagedProperty(value = "#{invitationListService}")
    private InvitationListService invitationListService;
	
	@ManagedProperty(value = "#{securityService}")
    private SecurityService securityService;
	
	
	
	@Override
	protected ITableDataProvider<Invitation> createDataProvider() {
		return new InvitationListProvider(invitationListService,this);
	}


	private Invitation selectedInvitation;
	
	private InvitationStatus statusFilter;
	private String groupFilter;
	
	
	private String inviteEmail;
	private Group inviteGroup;

	private int tabIndex=0;
	

	public Invitation getNewEntity() {
		return new Invitation();
	}
	
	
	
	public void inviteUser() throws SpringSecurityException {
		if (inviteEmail==null) {
			throw new IllegalStateException("Invite email was null");
		}
		if (inviteGroup==null) {
			throw new IllegalStateException("Invite group was null");
		}
		
		String userName = getAuthenticatedUserName();
		if (userName==null) {
			throw new IllegalStateException("Authenticated user name was null");
		}
		
		UserProfile invitor = securityService.findUserProfile(userName);
		if (invitor==null) {
			throw new IllegalStateException("UserProfile not found: "+userName);
		}
		if (!securityService.hasRole(userName,Group.ROLE_ADMIN)) {
			throw new IllegalStateException("User is not an administrator: "+userName);
		}
		
		tabIndex=1;
		
		invitationService.inviteUser(inviteEmail, invitor, inviteGroup);
		RequestContext.getCurrentInstance().execute("PF('userInviteDialog').hide();");
		
	}
	public String cancelInvitation() {
		if (selectedInvitation==null) {
			throw new IllegalStateException("Invitation was null");
		}
		String userName = getAuthenticatedUserName();
		if (userName==null) {
			throw new IllegalStateException("Authenticated user name was null");
		}
		
		if (!securityService.hasRole(userName,Group.ROLE_ADMIN)) {
			throw new IllegalStateException("User is not an administrator: "+userName);
		}
		tabIndex=1;
		
		invitationService.cancelInvitation(selectedInvitation.getId(),userName);
		return "/admin/users?faces-redirect=true";
		
	}
	
	public String resendInvitation() throws SpringSecurityException {
		if (selectedInvitation==null) {
			throw new IllegalStateException("Invitation was null");
		}
		String userName = getAuthenticatedUserName();
		if (userName==null) {
			throw new IllegalStateException("Authenticated user name was null");
		}
		
		if (!securityService.hasRole(userName,Group.ROLE_ADMIN)) {
			throw new IllegalStateException("User is not an administrator: "+userName);
		}
		tabIndex=1;
		
		invitationService.resendInvitation(selectedInvitation.getId(),userName);
		return "/admin/users?faces-redirect=true";
		
	}
	
	
	public List<SelectItem> getStatusItems() {
		
		
		List<SelectItem> ret=new ArrayList<SelectItem>();
		for (InvitationStatus s:InvitationStatus.values()) {
			ret.add(new SelectItem(s, LocalizationHelper.getLocalizedMessage("msg", InvitationStatus.class.getName()+"@"+s.name())));
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

	
	public InvitationStatus getStatusFilter() {
		return statusFilter;
	}

	public void setStatusFilter(InvitationStatus statusFilter) {
		this.statusFilter = statusFilter;
	}

	

	public String getGroupFilter() {
		return groupFilter;
	}

	public void setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
	}

	public String getInviteEmail() {
		return inviteEmail;
	}

	public void setInviteEmail(String inviteEmail) {
		this.inviteEmail = inviteEmail;
	}

	public Group getInviteGroup() {
		return inviteGroup;
	}

	public void setInviteGroup(Group inviteGroup) {
		this.inviteGroup = inviteGroup;
	}



	public InvitationService getInvitationService() {
		return invitationService;
	}



	public void setInvitationService(InvitationService invitationService) {
		this.invitationService = invitationService;
	}



	public Invitation getSelectedInvitation() {
		return selectedInvitation;
	}



	public void setSelectedInvitation(Invitation selectedInvitation) {
		this.selectedInvitation = selectedInvitation;
	}



	public InvitationListService getInvitationListService() {
		return invitationListService;
	}



	public void setInvitationListService(InvitationListService invitationListService) {
		this.invitationListService = invitationListService;
	}



	public SecurityService getSecurityService() {
		return securityService;
	}



	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}



	public int getTabIndex() {
		return tabIndex;
	}



	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}



	
}
