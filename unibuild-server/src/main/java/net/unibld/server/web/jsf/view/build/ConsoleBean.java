package net.unibld.server.web.jsf.view.build;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import net.unibld.server.entities.build.BuildLog;
import net.unibld.server.service.build.BuildQueue;
import net.unibld.server.service.build.BuildStateManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A managed bean to provide an output console of the current or last build run. The console gets
 * refreshed using periodic client polling calling getLogs().
 * @author andor
 *
 */
@ManagedBean(name="consoleBean")
@ViewScoped
public class ConsoleBean {
	private static final Logger LOG=LoggerFactory.getLogger(ConsoleBean.class);
	
	@ManagedProperty(value = "#{buildStateManager}")
    private BuildStateManager buildStateManager;
	
	@ManagedProperty(value = "#{buildQueue}")
    private BuildQueue buildQueue;
	
	
	
	/**
	 * Returns a list of build logs to display in the console datatable.
	 * @return List of build logs to display in the console datatable.
	 */
	public List<BuildLog> getLogs() {
		String buildId = buildQueue.getCurrentBuildId();
		if (buildId==null) {
			buildId=buildQueue.getLastBuildId();
		}
		if (buildId==null) {
			return null;
		}
		return buildStateManager.getBuildLogs(buildId);
	}

	
	
	public void refresh() {
		LOG.debug("Refreshing console...");
	}


	public BuildQueue getBuildQueue() {
		return buildQueue;
	}



	public void setBuildQueue(BuildQueue buildQueue) {
		this.buildQueue = buildQueue;
	}



	public BuildStateManager getBuildStateManager() {
		return buildStateManager;
	}



	public void setBuildStateManager(BuildStateManager buildStateManager) {
		this.buildStateManager = buildStateManager;
	}



}
