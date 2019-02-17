package net.unibld.server.web.jsf.view.build;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Build;
import net.unibld.server.service.build.BuildListService;
import net.unibld.server.web.jsf.FacesMessageHelper;
import net.unibld.server.web.jsf.datatable.ITableDataProvider;
import net.unibld.server.web.jsf.datatable.build.BuildListProvider;
import net.unibld.server.web.jsf.view.AbstractModelLazyTableBean;
import net.unibld.server.web.jsf.view.project.ProjectConfigCacheBean;

/**
 * A managed bean to list build runs
 * @author andor
 * @see Build
 *
 */
@ManagedBean(name="buildTableBean")
@ViewScoped
public class BuildTableBean extends AbstractModelLazyTableBean<Build> {
	private static final Logger LOG=LoggerFactory.getLogger(BuildTableBean.class);
	
	@ManagedProperty(value = "#{buildListService}")
    private BuildListService buildListService;
	
	@ManagedProperty(value = "#{projectConfigCacheBean}")
    private ProjectConfigCacheBean projectConfigCacheBean;

	
	private Boolean successFilter;

	@Override
	protected ITableDataProvider<Build> createDataProvider() {
		return new BuildListProvider(buildListService);
	}


	



	public Boolean getSuccessFilter() {
		return successFilter;
	}




	public void setSuccessFilter(Boolean successFilter) {
		this.successFilter = successFilter;
	}

	public String showLog() throws UnsupportedEncodingException {
		if (selectedItem==null) {
			return null;
		}
		if (selectedItem.getLogFilePath()==null) {
			FacesMessageHelper.addError("No log file found");
			return null;
		}
		LOG.info("Showing log of build {} at: {}...",selectedItem.getId(),selectedItem.getLogFilePath());
		return "/showlog?faces-redirect=true&log="+new String(Base64.encodeBase64URLSafe(selectedItem.getLogFilePath().getBytes("UTF-8")),"UTF-8");
	}


	public boolean isGoalConfirmed(Build build) {
		LOG.debug("Checking goal to confirm: {}", build.getGoal());
		ProjectConfig conf = projectConfigCacheBean.getProjectConfig(build.getProject());
		List<BuildGoalConfig> goals = conf.getGoalsConfig().getGoals();
		if (goals!=null) {
			for (BuildGoalConfig goal:goals) {
				if (goal.getName().equals(build.getGoal())) {
					LOG.debug("Goal {} confirmation: {}", build.getGoal(),goal.isConfirm());
					return goal.isConfirm();
				}
			}
		}
		LOG.warn("Goal not found: {}",build.getGoal());
		return false;
	}


	public BuildListService getBuildListService() {
		return buildListService;
	}




	public void setBuildListService(BuildListService buildListService) {
		this.buildListService = buildListService;
	}






	public ProjectConfigCacheBean getProjectConfigCacheBean() {
		return projectConfigCacheBean;
	}






	public void setProjectConfigCacheBean(ProjectConfigCacheBean projectConfigCacheBean) {
		this.projectConfigCacheBean = projectConfigCacheBean;
	}
}
