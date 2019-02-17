package net.unibld.server.web.jsf.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.service.security.passwordchange.PasswordChangeService;
import net.unibld.server.web.jsf.FacesMessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@ManagedBean(name="passwordChangeBean")
@ViewScoped
public class PasswordChangeBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(PasswordChangeBean.class);
	   
	
	private String code;
	private String password;
	private String passwordAgain;
	private UserTicket ticket;
	private boolean success;
	
	@ManagedProperty(value = "#{passwordChangeService}")
    private PasswordChangeService passwordChangeService;
	
	public boolean isCodeValid() {
		if (this.ticket!=null) {
			return true;
		}
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String cp = req.getParameter("code");
		
		if (!StringUtils.isEmpty(cp)) {
			try {
				this.ticket = passwordChangeService.getChangePasswordTicket(cp);
				if (this.ticket!=null) {
					this.code=cp;
				}
			} catch (IllegalArgumentException ex) {
				//do nothing
			} catch (Exception e) {
				LOGGER.error("Failed to get password change ticket",e);
			}
			return ticket!=null;
		}
		return false;
	}
	public void validateCode() {
		LOGGER.info("Validating password change code: {}...",code);
		try {
			this.ticket = passwordChangeService.getChangePasswordTicket(code);
		} catch (Exception e) {
			LOGGER.error("Failed to get password change ticket",e);
		}
		
	}
	public void changePassword() {
		if (!StringUtils.isEmpty(code)) {
			UserTicket ticket = null;
			try {
				ticket=passwordChangeService.getChangePasswordTicket(code);
			} catch (Exception e) {
				LOGGER.error("Failed to get password change ticket",e);
			}
			if (ticket==null) {
				FacesMessageHelper.addError("Invalid password change code");
				return;
			}
		} else {
			FacesMessageHelper.addError("No password change code specified");
			return;
		}
		if (!password.equals(passwordAgain)) {
			FacesMessageHelper.addError("The two passwords do not match");
			return;
		}
		
		
		try {
			LOGGER.info("Changing password with password change code: {}",code);
			String uid = passwordChangeService.changePassword(code, password);
			FacesMessageHelper.addInfo("Password changed successfully for : "+uid);
			success=true;
		} catch (Exception e) {
			LOGGER.error("Failed to get password change ticket",e);
			FacesMessageHelper.addError("Password change error: "+e.getMessage());
			success=false;
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordAgain() {
		return passwordAgain;
	}

	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}

	public PasswordChangeService getPasswordChangeService() {
		return passwordChangeService;
	}

	public void setPasswordChangeService(PasswordChangeService passwordChangeService) {
		this.passwordChangeService = passwordChangeService;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}


	

	
}
