package net.unibld.server.service.build;

import net.unibld.core.persistence.model.Build;
import net.unibld.server.entities.build.RemoteBuild;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoteBuildListenerFactory {
	private static final Logger LOG=LoggerFactory.getLogger(RemoteBuildListenerFactory.class);
	

	public class RemoteBuildListener implements BuildQueueListener {
		private RemoteBuild remoteBuild;

		public RemoteBuildListener(RemoteBuild rb) {
			this.remoteBuild=rb;
		}
		@Override
		public void progress(String buildId,int perc,int taskIdx) {
			// do nothing
		}
		
		@Override
		public void errorLogCreated(String buildId,String taskClassName, String logFilePath) {
			// do nothing
			
		}
		
		@Override
		public void buildFailed(String buildId, String taskClassName, int taskIdx,
				Exception ex) {
			LOG.info("Remote build {} failed with build: {}",remoteBuild.getId(),buildId);
			remoteBuildService.remoteBuildFailed(buildId);
		}
		
		@Override
		public void buildCompleted(String buildId) {
			LOG.info("Remote build {} completed with build: {}",remoteBuild.getId(),buildId);
			remoteBuildService.remoteBuildCompleted(buildId);
		}

		@Override
		public void buildStarted(Build b) {
			LOG.info("Remote build {} started with build: {}",remoteBuild.getId(),b.getId());
			remoteBuildService.remoteBuildStarted(remoteBuild.getId(),b);
		}
	}
	@Autowired
	private RemoteBuildService remoteBuildService;
	
	public RemoteBuildListener createListener(RemoteBuild rb) {
		return new RemoteBuildListener(rb);
	}
}
