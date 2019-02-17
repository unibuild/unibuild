package net.unibld.server.web.jsf.model;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import net.unibld.server.entities.security.Group;
import net.unibld.server.web.jsf.LocalizationHelper;

public class SelectItemHelper {


	public static List<SelectItem> getUserRoleItems() {
		List<SelectItem> ret=new ArrayList<SelectItem>();
		for (Group s:Group.values()) {
			ret.add(new SelectItem(s, LocalizationHelper.getLocalizedMessage("msg", Group.class.getName()+"@"+s.name())));
		}
		return ret;
	}
	

}
