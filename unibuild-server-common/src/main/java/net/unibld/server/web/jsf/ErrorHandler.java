package net.unibld.server.web.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class ErrorHandler {
	
	public String getStatusCode(){
		String val = String.valueOf((Integer)FacesContext.getCurrentInstance().getExternalContext().
				getRequestMap().get("javax.servlet.error.status_code"));
		return val;
	}

	public String getMessage(){
		String val =  (String)FacesContext.getCurrentInstance().getExternalContext().
			getRequestMap().get("javax.servlet.error.message");
		return val;
	}

	public String getExceptionType(){
		Object object = FacesContext.getCurrentInstance().getExternalContext().
			getRequestMap().get("javax.servlet.error.exception_type");
		if (object!=null) {
			return object.toString();
		}
		return null;
	}

	public String getException(){
		Exception exception = (Exception)FacesContext.getCurrentInstance().getExternalContext().
			getRequestMap().get("javax.servlet.error.exception");
		if (exception!=null) {
			return exception.getClass().getName()+": "+exception.getMessage();
		}
		return null;
	}

	public String getRequestURI(){
		return (String)FacesContext.getCurrentInstance().getExternalContext().
			getRequestMap().get("javax.servlet.error.request_uri");
	}

	public String getServletName(){
		return (String)FacesContext.getCurrentInstance().getExternalContext().
			getRequestMap().get("javax.servlet.error.servlet_name");
	}

}