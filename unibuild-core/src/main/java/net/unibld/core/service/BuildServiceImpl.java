package net.unibld.core.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.unibld.core.BuildProject;
import net.unibld.core.build.BuildException;
import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.BuildTaskResult;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.repositories.BuildRepository;
import net.unibld.core.repositories.BuildTaskResultRepository;

/**
 * Persistence service for entity {@link Build}, implemented as a Spring {@link Service}. 
 * @author andor
 *
 */
@Service("buildService")
public class BuildServiceImpl implements BuildService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BuildServiceImpl.class);
	
	@Autowired
	private BuildRepository buildRepo;
	@Autowired
	private BuildTaskResultRepository buildTaskResultRepo;
	@Autowired
	private ProjectService projectPersistence;

	

	@Override
	@Transactional
	public Build startBuild(String buildId,BuildProject project,String path,String goal,String user) {
		if (project==null) {
			throw new IllegalArgumentException("Project was null");
		}
		if (goal==null) {
			throw new IllegalArgumentException("Goal was null");
		}
		LOGGER.info("Starting build {} of {}: {}...", buildId, project.getName(), goal);
		Project p=projectPersistence.saveProject(project,path,user);
		
		Build b=new Build();
		b.setId(buildId);
		b.setStartDate(new Date());
		b.setProductName(project.getProjectConfig().getProductName());
		b.setProject(p);
		b.setBuildNumber(p.getBuildNumber());
		
		
		b.setGoal(goal);
		List<BuildGoalConfig> customGoals = project.getProjectConfig().getGoalsConfig().getGoals();
		BuildGoalConfig cfg=null;
		for (BuildGoalConfig bgc:customGoals) {
			if (bgc.getName().equals(goal)) {
				cfg=bgc;
				break;
			}
		}
		
		if (cfg!=null) {
			b.setTaskCount(cfg.getTasks().getTasks().size());
		}
		
		
		b.setCreatorUser(user);
		buildRepo.save(b);
		return b;
	}

	@Override
	@Transactional
	public void buildCompleted(String buildId) {
		Build b = buildRepo.findOne(buildId);
		if (b==null) {
			throw new BuildException("Build not found: "+buildId);
		}
		LOGGER.info("Saving build completed: {}...", buildId);

		b.setCompleteDate(new Date());
		b.setSuccessful(true);
		b.setErrorMessage(null);
		b.setErrorStackTrace(null);
		buildRepo.save(b);
	}

	@Override
	@Transactional
	public void buildFailed(String buildId,String taskResultId, String taskClass,int taskIdx,Throwable t,String logFilePath) {
		Build b = buildRepo.findOne(buildId);
		
		if (b==null) {
			throw new BuildException("Build not found: "+buildId);
		}
		LOGGER.info("Saving build error: {}...", buildId);

		if (t!=null&&t.getMessage()!=null) {
			b.setErrorMessage(t.getMessage());
		}
		b.setSuccessful(false);
		b.setCompleteDate(new Date());
		if (t!=null) {
			String stackTrace = ExceptionUtils.getStackTrace(t);
			if (stackTrace!=null) {
				if (stackTrace.length()>Build.MAX_STACK_TRACE_LEN) {
					stackTrace=stackTrace.substring(0,Build.MAX_STACK_TRACE_LEN-5)+"...";
				}
				b.setErrorStackTrace(stackTrace);
			}
		}
		b.setFailedTaskClass(taskClass);
		b.setFailedTaskIndex(taskIdx);
		b.setLogFilePath(logFilePath);
		buildRepo.save(b);
		
		if (taskResultId!=null) {
			BuildTaskResult taskResult = buildTaskResultRepo.findOne(taskResultId);
			if (taskResult!=null) {
				taskResult.setCompleteDate(b.getCompleteDate());
				taskResult.setErrorMessage(b.getErrorMessage());
				taskResult.setErrorStackTrace(b.getErrorStackTrace());
				taskResult.setLogFilePath(b.getLogFilePath());
				taskResult.setSuccessful(false);
				buildTaskResultRepo.save(taskResult);
			} else {
				LOGGER.warn("Build task result not found: {}", taskResultId);
			}
		}
 	}

	public Build findBuild(String id) {
		return buildRepo.findOne(id);
	}

	
	@Transactional
	public void deleteAllBuilds() {
		LOGGER.info("Deleting all build records...");

		buildRepo.deleteAll();
	}

	@Override
	@Transactional
	public void cancelBuild(String buildId) {
		LOGGER.info("Cancelling build: {}...", buildId);

		Build b = buildRepo.findOne(buildId);
		if (b==null) {
			throw new BuildException("Build not found: "+buildId);
		}
		
		if (b.getCompleteDate()!=null) {
			throw new BuildException("Build already completed");
		}
		if (b.getCancelDate()!=null) {
			throw new BuildException("Build already cancelled");
		}
		if (b.getStartDate()==null) {
			throw new BuildException("Build not started");
		}
		b.setCancelDate(new Date());
		buildRepo.save(b);
	}

	@Override
	public boolean isBuildCancelled(String buildId) {
		Build b = buildRepo.findOne(buildId);
		if (b==null) {
			throw new BuildException("Build not found: "+buildId);
		}
		
		return b.getCancelDate()!=null;
	}

	@Override
	@Transactional
	public BuildTaskResult taskStarted(String buildId, String className, String taskName, int taskIdx) {
		Build b = buildRepo.findOne(buildId);
		if (b==null) {
			throw new BuildException("Build not found: "+buildId);
		}
		LOGGER.info("Saving task start: {} in build {}...", taskName, buildId);
		BuildTaskResult tr=new BuildTaskResult();
		tr.setId(UUID.randomUUID().toString());
		tr.setStartDate(new Date());
		tr.setTaskClass(className);
		tr.setTaskName(taskName);
		tr.setTaskIdx(taskIdx);
		tr.setBuild(b);
		return buildTaskResultRepo.save(tr);
	}

	@Override
	@Transactional
	public BuildTaskResult taskCompleted(String taskResultId) {
		BuildTaskResult taskResult = buildTaskResultRepo.findOne(taskResultId);
		if (taskResult==null) {
			throw new IllegalArgumentException("Invalid BuildTaskResult id: "+taskResultId);
		}
		taskResult.setCompleteDate(new Date());
		taskResult.setSuccessful(true);
		return buildTaskResultRepo.save(taskResult);
	}

	@Override
	public List<BuildTaskResult> getBuildTaskResults(Build build) {
		return buildTaskResultRepo.findByBuildOrderByStartDateAsc(build);
	}
}
