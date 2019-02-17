package net.unibld.server.web.jsf.view.build;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import net.unibld.core.persistence.model.Build;
import net.unibld.server.service.build.BuildQueue;
import net.unibld.server.service.build.BuildStateManager;
import net.unibld.server.web.jsf.FacesMessageHelper;
import net.unibld.server.web.jsf.view.project.ProjectRunnerBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JSF managed bean to monitor build progresses. It gets polled by the header of the page
 * periodically to be able to refresh build progress automatically on every screen.
 * @author andor
 *
 */
@ManagedBean(name="buildProgressBean")
@ViewScoped
public class BuildProgressBean  {
	private static final Logger LOG=LoggerFactory.getLogger(ProjectRunnerBean.class);
	
	@ManagedProperty(value = "#{buildStateManager}")
    private BuildStateManager buildStateManager;
	
	
	@ManagedProperty(value = "#{buildQueue}")
    private BuildQueue buildQueue;
	
	@ManagedProperty(value = "#{passwordReaderBean}")
    private PasswordReaderBean passwordReaderBean;
	

	private boolean completeDisplayed;
	
	public boolean isRunning() {
		return buildQueue.isRunning();
	}
	public boolean isRunningOrCompleted() {
		return buildQueue.isRunningOrCompleted();
	}
	
	public boolean isBuildRunning(Build build) {
		return buildQueue.isBuildRunning(build.getId());
	}
	
	public String getCurrentBuildLabel() {
		return buildQueue.getCurrentOrLastBuildLabel();
	}

	public BuildQueue getBuildQueue() {
		return buildQueue;
	}

	public void setBuildQueue(BuildQueue buildQueue) {
		this.buildQueue = buildQueue;
	}

	public void cancelBuild() {
		if (buildQueue.isRunning()) {
			LOG.info("Cancelling current build...");
			try {
				buildQueue.cancelCurrent();
				FacesMessageHelper.addError("Build cancelled.");
			} catch (Exception ex) {
				LOG.error("Cancel build failed",ex);
				FacesMessageHelper.addError(ex.getMessage());
			}
		}
	}
	
	public void cancelQueue() {
		if (buildQueue.getWaitingQueueSize()>0) {
			LOG.info("Cancelling items waiting in queue...");
			try {
				buildQueue.cancelWaiting();
				FacesMessageHelper.addError("Queue items cancelled.");
			} catch (Exception ex) {
				LOG.error("Cancel queue failed",ex);
				FacesMessageHelper.addError(ex.getMessage());
			}
		}
	}
	
	public void onComplete(javax.faces.event.AjaxBehaviorEvent e) {
		if (!completeDisplayed) {
			FacesMessageHelper.addInfo("Build completed");
			completeDisplayed=true;
		}
	}

    public Integer getProgress() {
    	String buildId = buildQueue.getCurrentBuildId();
		if (buildId==null) {
			buildId=buildQueue.getLastBuildId();
		}
		if (buildId==null) {
			return null;
		}
		if (passwordReaderBean.isPasswordRequested()) {
			passwordReaderBean.showPasswordDialog();
		} else if (passwordReaderBean.isDialogCloseRequested()) {
			passwordReaderBean.hidePasswordDialog();
		}
        return buildStateManager.getProgress(buildId);
    }
 
    public int getQueueSize() {
    	return buildQueue.getWaitingQueueSize();
    }
    
    public String getQueueContent() {
    	return buildQueue.getWaitingQueueContent();
    }
	public BuildStateManager getBuildStateManager() {
		return buildStateManager;
	}
	public void setBuildStateManager(BuildStateManager buildStateManager) {
		this.buildStateManager = buildStateManager;
	}
	public PasswordReaderBean getPasswordReaderBean() {
		return passwordReaderBean;
	}
	public void setPasswordReaderBean(PasswordReaderBean passwordReaderBean) {
		this.passwordReaderBean = passwordReaderBean;
	}
   
	

}
