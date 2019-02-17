package net.unibld.server.service.build;

import java.util.List;

import net.unibld.core.persistence.model.Build;
import net.unibld.server.entities.build.BuildLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component("buildStateManager")
public class BuildStateManager implements BuildQueueListener,LogConsumer {
	private static final Logger LOGGER=LoggerFactory.getLogger(BuildStateManager.class);
	
	@Autowired
	private BuildCacheManager cache;
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private BuildLogService logService;
	
	@Override
	public void buildCompleted(String buildId) {
		BuildState state = cache.getBuildState(buildId);
		if (state==null) {
			throw new IllegalStateException("Build state not found: "+buildId);
		}
		state.setRunning(false);
		state.setCompleted(true);
		cache.putBuildState(buildId,state);
	}

	@Override
	public void buildFailed(String buildId, String taskClassName, int taskIdx,
			Exception ex) {
		BuildState state = cache.getBuildState(buildId);
		if (state==null) {
			throw new IllegalStateException("Build state not found: "+buildId);
		}
		state.setRunning(false);
		state.setCompleted(false);
		state.setFailedTaskClassName(taskClassName);
		state.setFailedTaskIdx(taskIdx);
		state.setFailureCause(ex);
		cache.putBuildState(buildId,state);
	}

	@Override
	public void errorLogCreated(String buildId,String taskClassName, String logFilePath) {
		BuildState state = cache.getBuildState(buildId);
		if (state==null) {
			throw new IllegalStateException("Build state not found: "+buildId);
		}
		state.setErrorLogPath(logFilePath);
		cache.putBuildState(buildId,state);
	}

	@Override
	public void buildStarted(Build b) {
		BuildState state = cache.getBuildState(b.getId());
		if (state==null) {
			state=new BuildState();
			state.setBuildId(b.getId());
		}
		state.setRunning(true);
		state.setCompleted(false);
		
		state.setProgress(0);
		cache.putBuildState(b.getId(),state);
	}

	@Override
	public void progress(String buildId,int perc,int taskIdx) {
		BuildState state = cache.getBuildState(buildId);
		if (state==null) {
			throw new IllegalStateException("Build state not found: "+buildId);
		}
		state.setProgress(perc);
		state.setCurrentTaskIdx(taskIdx);
		cache.putBuildState(buildId,state);
	}

	@Override
	public void addLogMessage(String buildId,BuildLog log) {
		log.setBuildId(buildId);
		logService.saveBuildLog(log);
	}

	@Override
	public void appendToLastLog(String buildId,String str) {
		BuildState state = cache.getBuildState(buildId);
		if (state==null) {
			throw new IllegalStateException("Build state not found: "+buildId);
		}
		List<BuildLog> logs = logService.getBuildLogs(buildId);
		if (logs.isEmpty()) {
			throw new IllegalStateException("No last log available");
		}
		BuildLog log = logs.get(logs.size()-1);
		log.append(str);
		
		cache.putBuildState(buildId,state);
		logService.saveBuildLog(log);
		
	}

	public List<BuildLog> getBuildLogs(String buildId) {
		return logService.getBuildLogs(buildId);
	}

	public Integer getProgress(String buildId) {
		BuildState state = cache.getBuildState(buildId);
		if (state==null) {
			return null;
		}
		return state.getProgress();
	}


}
