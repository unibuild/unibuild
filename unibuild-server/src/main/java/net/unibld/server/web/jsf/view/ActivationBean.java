package net.unibld.server.web.jsf.view;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.service.security.SpringSecurityException;
import net.unibld.server.service.security.invitation.InvitationService;
import net.unibld.server.web.jsf.FacesMessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

@ManagedBean(name="activationBean")
@ViewScoped
public class ActivationBean {
	private static final Logger LOG=LoggerFactory.getLogger(ActivationBean.class);
	
	private Invitation invitation;
	
	private String userName;
	private String password;
	private String passwordConfirm;
	
	private String authCode;
	
	@ManagedProperty(value = "#{invitationService}")
    private InvitationService invitationService;
	
	@ManagedProperty(value = "#{securityService}")
    private SecurityService securityService;
	
	@PostConstruct
	public void init() {
		HttpServletRequest req=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String code = req.getParameter("code");
		if (!StringUtils.isEmpty(code)) {
			this.invitation=invitationService.getInvitationSentByAuthCode(code);
			if (invitation==null) {
				LOG.warn("Invalid invitation id: "+code);
			}
		}
	}
	
	public void checkInvitationCode() {
		if (authCode!=null) {
			this.invitation=invitationService.getInvitationSentByAuthCode(authCode);
			if (invitation==null) {
				FacesMessageHelper.addError("Invalid invitation code: "+authCode);
			}
		} else {
			LOG.info("Auth code was null");
		}

	}
	
	public String activate() {
		if (!password.equals(passwordConfirm)) {
			FacesMessageHelper.addError("The two passwords do not match");
			return null;
		}
		
		UserProfile profile=new UserProfile();
		profile.setUserName(userName);
		profile.setEmail(invitation.getEmail());
		try {
			invitationService.registerInvitedUser(profile, invitation, password);
			
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					userName, password);
			Authentication auth = securityService.getAuthenticationManager().authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(auth);
			FacesMessageHelper.addInfo("Thanks for activating your user.");
			return "index?faces-redirect=true";
		} catch (Exception e) {
			LOG.error("Failed to register invited user",e);
			if (e instanceof SpringSecurityException&&e.getMessage().startsWith("User already exists")) {
				FacesMessageHelper.addError("Username already exists: "+userName);
			} else {
				FacesMessageHelper.addError(e.getMessage());
			}
			return null;
		}

	}

	public Invitation getInvitation() {
		return invitation;
	}

	public void setInvitation(Invitation invitation) {
		this.invitation = invitation;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public InvitationService getInvitationService() {
		return invitationService;
	}

	public void setInvitationService(InvitationService invitationService) {
		this.invitationService = invitationService;
	}


	public SecurityService getSecurityService() {
		return securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
