package net.unibld.server.web.jsf.view.log;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import net.unibld.server.entities.log.Log;
import net.unibld.server.service.log.LogService;
import net.unibld.server.web.jsf.LocalizationHelper;
import net.unibld.server.web.jsf.datatable.ITableDataProvider;
import net.unibld.server.web.jsf.datatable.log.LogListProvider;
import net.unibld.server.web.jsf.view.AbstractModelLazyTableBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ManagedBean(name="logTableBean")
@SessionScoped
public class LogTableBean extends AbstractModelLazyTableBean<Log> {
	private static final Logger LOG=LoggerFactory.getLogger(LogTableBean.class);
	
	@ManagedProperty(value = "#{logService}")
    private LogService logService;
	

	@Override
	protected ITableDataProvider<Log> createDataProvider() {
		return new LogListProvider(logService);
	}


	private Log selectedLog;

	

	public LogService getLogService() {
		return logService;
	}


	public void setLogService(LogService logService) {
		this.logService = logService;
	}


	public Log getSelectedLog() {
		return selectedLog;
	}


	public void setSelectedLog(Log selectedLog) {
		this.selectedLog = selectedLog;
	}


	
}
