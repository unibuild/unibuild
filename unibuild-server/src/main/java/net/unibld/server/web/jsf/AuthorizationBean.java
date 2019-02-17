package net.unibld.server.web.jsf;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.Group;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.web.WebAppVersionInfo;

@ManagedBean(name="authorizationBean")
@SessionScoped
public class AuthorizationBean {
	

	@ManagedProperty(value = "#{securityService}")
    private SecurityService securityService;
	
	public String getAuthenticatedUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication==null) {
			return null;
		}
		return authentication.getName();
	}
	
	public boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication==null) {
			return false;
		}
		return authentication.isAuthenticated();
	}
	
	
	public String getAuthenticatedUserRoles() {
		String uid = getAuthenticatedUserId();
		if (uid==null) {
			return null;
		}
		List<Authority> roles = securityService.getAuthorities(uid);
		StringBuilder b=new StringBuilder();
		int i=0;
		for (Authority r:roles) {
			if (i>0) {
				b.append(", ");
			}
			b.append(LocalizationHelper.getLocalizedMessage("msg", Group.class.getName()+"@"+r.getAuthority()));
			i++;
		}
		return b.toString();
	}
	
	public boolean isAdmin() {
		String uid=getAuthenticatedUserId();
		if (uid==null) {
			return false;
		}
		return securityService.hasRole(uid, Group.ROLE_ADMIN);
	}
	
	public boolean isAdminOrSuperUser() {
		String uid=getAuthenticatedUserId();
		if (uid==null) {
			return false;
		}
		return securityService.hasEitherRole(uid, Group.ROLE_ADMIN, Group.ROLE_SUPER_USER);
	}

	public SecurityService getSecurityService() {
		return securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
	
}
