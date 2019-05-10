package net.unibld.server.web.jsf.view.build;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.BuildTaskResult;
import net.unibld.core.persistence.model.BuildTestResult;
import net.unibld.core.persistence.model.BuildTestSuite;
import net.unibld.core.service.BuildService;
import net.unibld.core.service.BuildTestService;

@ManagedBean(name="buildResultsBean")
@ViewScoped
public class BuildResultsBean {
	private static final Logger LOG=LoggerFactory.getLogger(BuildResultsBean.class);
	
	@ManagedProperty(value = "#{buildService}")
    private BuildService buildService;
	@ManagedProperty(value = "#{buildTestService}")
    private BuildTestService buildTestService;

	private Build build;
	private BuildTestResult selectedTestResult;
	
	/**
	 * Initializes the bean by checking the request parameter 'id' for a {@link Build} id specified.
	 */
	@PostConstruct
	public void init() {
		 HttpServletRequest req=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		 String id=req.getParameter("id");
		 if (!StringUtils.isEmpty(id)) {
			loadBuild(id.trim());
			
		 } else {
			throw new IllegalArgumentException("Id not specified");
		 }
	}
	
	private void loadBuild(String id) {
		this.build = buildService.findBuild(id);
		if (build==null) {
			throw new IllegalArgumentException("Invalid build id: "+id);
		}
	}

	public List<BuildTaskResult> getTaskResults() {
		return buildService.getBuildTaskResults(build);
	}
	
	public List<BuildTestSuite> getTestSuites() {
		return buildTestService.getTestSuites(build);
	}
	
	public List<BuildTestResult> getTestResults(BuildTestSuite suite) {
		return buildTestService.getTestResults(suite);
	}
	
	public String getLimitedString(String str) {
		if (str==null) {
			return null;
		}
		if (str.length()<=255) {
			return str;
		}
		return str.substring(0,255)+" [...]";
	}

	public BuildService getBuildService() {
		return buildService;
	}

	public void setBuildService(BuildService buildService) {
		this.buildService = buildService;
	}

	public Build getBuild() {
		return build;
	}

	public BuildTestService getBuildTestService() {
		return buildTestService;
	}

	public void setBuildTestService(BuildTestService buildTestService) {
		this.buildTestService = buildTestService;
	}

	public BuildTestResult getSelectedTestResult() {
		return selectedTestResult;
	}

	public void setSelectedTestResult(BuildTestResult selectedTestResult) {
		this.selectedTestResult = selectedTestResult;
	}
	
}
