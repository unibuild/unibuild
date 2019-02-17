package net.unibld.server.service.build;

import java.util.List;

import net.unibld.server.entities.build.BuildLog;

public interface BuildLogService {
	BuildLog saveBuildLog(BuildLog log);

	List<BuildLog> getBuildLogs(String buildId);
}
