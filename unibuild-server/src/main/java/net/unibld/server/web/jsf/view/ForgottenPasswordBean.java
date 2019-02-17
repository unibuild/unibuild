package net.unibld.server.web.jsf.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import net.unibld.server.service.security.SecurityService;
import net.unibld.server.service.security.mail.SecurityMailService;
import net.unibld.server.web.jsf.FacesMessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name="forgottenPasswordBean")
@ViewScoped
public class ForgottenPasswordBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(ForgottenPasswordBean.class);
	   
	
	private String email;
	
	@ManagedProperty(value = "#{securityService}")
    private SecurityService securityService;
	

	@ManagedProperty(value = "#{securityMailService}")
    private SecurityMailService securityMailService;
	
	
	public void send() {
		if (!securityService.isEmailExisting(email)) {
			FacesMessageHelper.addError("E-mail address does not exist: "+email);
			return;
		}
	
		try {
			LOGGER.info("Sending forgotten password email: {}",email);
			securityMailService.sendForgottenPasswordMail(email);
			
			FacesMessageHelper.addInfo("Forgotten password mail sent to: "+email);
		
			email=null;
		} catch (Exception ex) {
			LOGGER.error("Failed to send forgotten password mail",ex);
			FacesMessageHelper.addError(ex.getMessage());
		}
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public SecurityService getSecurityService() {
		return securityService;
	}


	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}


	public SecurityMailService getSecurityMailService() {
		return securityMailService;
	}


	public void setSecurityMailService(SecurityMailService securityMailService) {
		this.securityMailService = securityMailService;
	}



}
