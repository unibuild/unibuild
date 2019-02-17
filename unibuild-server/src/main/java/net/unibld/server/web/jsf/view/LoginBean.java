package net.unibld.server.web.jsf.view;



import java.io.IOException;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Managed bean to login users to the UniBuild web app using Spring Security.
 * @author andor
 *
 */
@ManagedBean(name="loginBean")
@SessionScoped
public class LoginBean {
		
	private static final Logger LOG=LoggerFactory.getLogger(LoginBean.class);
	
	
	private String userName;
	private String password;

	
	private String displayName;


	
	public String doLogin() throws ServletException, IOException {
		LOG.info("Authenticating {}",getUserName());
		
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		 
		 HttpServletRequest request = (HttpServletRequest) context.getRequest();
	//	 traceRequest(request);
		
		RequestDispatcher dispatcher = request
		  .getRequestDispatcher("/authenticate");
		 
		 dispatcher.forward(request,
		  (ServletResponse) context.getResponse());
		 
		 FacesContext.getCurrentInstance().responseComplete();
		 
		 return null;
	}
	
	
	public String getServerTime() {
		return new Date().toString();
	}


	public String getUserName() {
		return userName;
	}

	


	

	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

	


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	

	public void logout() throws IOException {
		SecurityContextHolder.clearContext();
		FacesContext.getCurrentInstance().getExternalContext().redirect("/logout");
	}




}
