package net.unibld.server.web.jsf;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * A helper class for constucting error and info messages with a localization property
 * key of from an exception
 * @author andor
 *
 */
public class FacesMessageHelper {
	
	public static void addError(String msg) {
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}
	public static void addInfo(String msg) {
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}

	/**
	 * @return The current locale in the underlying JSF context
	 */
	public static String getCurrentLocale() {
		String localeCode=FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		return localeCode;
	}
	
	/**
	 * Adds an error message to {@link FacesContext} with the specified parameters 
	 * (key=[formName]:[fieldName]).
	 * @param formName Form name
	 * @param fieldName Field name
	 * @param message Error message
	 */
	public static void addErrorMessage(String formName, String fieldName,String message) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(formName+":"+fieldName, new FacesMessage(FacesMessage.SEVERITY_ERROR,message,null));
		
	}
	
	public static void addInfoMessage(String formName, String fieldName,String message) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		context.addMessage(formName+":"+fieldName, new FacesMessage(FacesMessage.SEVERITY_INFO,message,null));
		
	}

	


	public static void removerErrors() {
		Iterator<FacesMessage> msgs = FacesContext.getCurrentInstance().getMessages();
		if (msgs.hasNext()) {
			msgs.remove();  
		}
	}
	public static void addInfo(UIComponent component, String msg) {
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage(component.getClientId(), facesMessage);
	}
	
	public static void addError(UIComponent component, String msg) {
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(component.getClientId(), facesMessage);
	}
}
