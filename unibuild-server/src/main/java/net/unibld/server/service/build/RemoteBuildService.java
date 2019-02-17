package net.unibld.server.service.build;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.server.entities.build.RemoteBuild;

public interface RemoteBuildService {
	RemoteBuild startRemoteBuild(String id,Project project, String goal,String userId);
	RemoteBuild remoteBuildFailed(String buildId);
	RemoteBuild remoteBuildCompleted(String buildId);
	RemoteBuild remoteBuildStarted(String remoteBuildId, Build build);
	RemoteBuild findRemoteBuild(String id);
}
