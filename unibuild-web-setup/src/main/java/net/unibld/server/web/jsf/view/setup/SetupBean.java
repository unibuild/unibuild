package net.unibld.server.web.jsf.view.setup;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import net.unibld.core.persistence.DatabaseType;
import net.unibld.server.service.setup.SetupModel;
import net.unibld.server.service.setup.SetupService;
import net.unibld.server.web.HttpUtils;
import net.unibld.server.web.jsf.FacesContextHelper;
import net.unibld.server.web.jsf.FacesMessageHelper;

import org.primefaces.event.FlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JSF managed bean that controls web-based setup.
 * @author andor
 *
 */
@ManagedBean(name="setupBean")
@SessionScoped
public class SetupBean implements Serializable {
	private static final Logger LOGGER=LoggerFactory.getLogger(SetupBean.class);
	
	
	private SetupModel model;
	
	private String passwordAgain;

	@PostConstruct
	public void init() {
		
		if (setupService.isPropertiesExisting()) {
			try {
				model=setupService.loadProperties();
			} catch (IOException e) {
				LOGGER.error("Failed to load global config",e);
			}
		} else {
			model=new SetupModel();
			model.getDatabase().setType(DatabaseType.MySQL);
		}
		
	}
	
	@ManagedProperty(value = "#{setupService}")
    private SetupService setupService;
	
	
	public String save() {
		try {
			model.getMail().setReturnHost(HttpUtils.getBaseUrl((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()));
			setupService.saveProperties(model);
			FacesMessageHelper.addInfo("Configuration successful.");
			return "success";
		} catch (Exception ex) {
			LOGGER.error("Configuration error",ex);
			FacesMessageHelper.addError("Configuration error: "+ex.getMessage());
			return null;
		}
    }
	private boolean skip;


	private String testAddress;
	
    public boolean isSkip() {
        return skip;
    }
 
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
     
    public String onFlowProcess(FlowEvent event) {
        if(skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }

	public SetupModel getModel() {
		return model;
	}

	public void setModel(SetupModel model) {
		this.model = model;
	}
	
	public List<SelectItem> getDatabaseTypeItems() {
		List<SelectItem> ret=new ArrayList<SelectItem>();
		for (DatabaseType type:DatabaseType.values()) {
			if (type!=DatabaseType.SQLite&& type!=DatabaseType.Oracle) {
				ret.add(new SelectItem(type, type.name()));
			}
		}
		return ret;
	}
	
	/**
	 * Tests the specified database connection
	 */
	public void testDatabaseConnection() {
		LOGGER.info("Testing database connection...");
		try {
			setupService.testDatabaseConnection(model.getDatabase());
			LOGGER.info("Database connection test succeeded");
			FacesMessageHelper.addInfo("Database connection test succeeded");
		} catch (Exception ex) {
			LOGGER.error("Database connection test failed",ex);
			FacesMessageHelper.addError("Database connection test failed: "+ex.getMessage());
		}
	}
	
	/**
	 * Tests the specified mail account
	 */
	public void testMail() {
		LOGGER.info("Testing SMTP mails...");
		try {
			setupService.testMail(model.getMail(),testAddress);
			LOGGER.info("Mail test succeeded");
			FacesMessageHelper.addInfo("Mail test succeeded, you should receive a mail soon.");
		} catch (Exception ex) {
			LOGGER.error("Mail test failed",ex);
			FacesMessageHelper.addError("Mail test failed: "+ex.getMessage());
		}
	}

	public SetupService getSetupService() {
		return setupService;
	}

	public void setSetupService(SetupService setupService) {
		this.setupService = setupService;
	}

	public void copyUrl(String url) {
		model.getDatabase().setUrl(url);
	}
	
	public void databaseTypeChanged() {
		LOGGER.info("Database type changed to: {}",model.getDatabase().getType());
		model.getDatabase().setUrl(null);
		model.getDatabase().setUser(null);
		model.getDatabase().setPassword(null);
		
		UrlComposerBean urlComposerBean=FacesContextHelper.findBean("urlComposerBean");
		urlComposerBean.updateDefaults(model.getDatabase().getType());
	}

	public String getTestAddress() {
		return testAddress;
	}

	public void setTestAddress(String testAddress) {
		this.testAddress = testAddress;
	}

	public String getPasswordAgain() {
		return passwordAgain;
	}

	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}


}
