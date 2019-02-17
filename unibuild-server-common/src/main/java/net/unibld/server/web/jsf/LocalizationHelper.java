package net.unibld.server.web.jsf;

import javax.faces.context.FacesContext;

public class LocalizationHelper {
	public static String getLocalizedMessage(String bundleVar,String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().evaluateExpressionGet(context, String.format("#{%s['%s']}",bundleVar,key), String.class);
	}
}
