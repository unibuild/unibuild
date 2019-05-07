package net.unibld.server.web.jsf;

import java.lang.reflect.InvocationTargetException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.springframework.http.HttpStatus;

import com.sun.faces.mgbean.ManagedBeanCreationException;

@ManagedBean
@RequestScoped
public class ErrorHandler {
	
	public String getStatusCode(){
		Throwable inner = getInnerException(false);
		if (inner!=null) {
			if (inner instanceof SecurityException) {
				return String.valueOf(HttpStatus.FORBIDDEN.value());
			}
			if (inner instanceof IllegalArgumentException) {
				return String.valueOf(HttpStatus.BAD_REQUEST.value());
			}
			return String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return String.valueOf((Integer)FacesContext.getCurrentInstance().getExternalContext().
				getRequestMap().get("javax.servlet.error.status_code"));
	}

	public String getMessage(){
		Throwable inner = getInnerException(false);
		if (inner!=null) {
			return inner.getMessage();
		}
		return  (String)FacesContext.getCurrentInstance().getExternalContext().
			getRequestMap().get("javax.servlet.error.message");
	}

	public String getExceptionType(){
		Throwable inner = getInnerException(false);
		if (inner!=null) {
			return inner.getClass().getName();
		}
		Object object = FacesContext.getCurrentInstance().getExternalContext().
			getRequestMap().get("javax.servlet.error.exception_type");
		if (object!=null) {
			return object.toString();
		}
		return null;
	}

	private Throwable getInnerException(boolean returnOriginal) {
		Exception exception = (Exception)FacesContext.getCurrentInstance().getExternalContext().
				getRequestMap().get("javax.servlet.error.exception");
		if (exception instanceof ManagedBeanCreationException && exception.getCause()!=null) {
			ManagedBeanCreationException mbce=(ManagedBeanCreationException) exception;
			Throwable cause = mbce.getCause().getCause();
			if (cause instanceof InvocationTargetException) {
				InvocationTargetException ite=(InvocationTargetException) cause;
				return ite.getTargetException();
				
			}
		}
		
		return returnOriginal?exception:null;
	}

	public String getExceptionTypeAndMessage(){
		Throwable exception = getInnerException(true);
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