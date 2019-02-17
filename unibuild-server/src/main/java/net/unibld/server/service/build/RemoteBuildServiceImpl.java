package net.unibld.server.service.build;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.server.entities.build.RemoteBuild;
import net.unibld.server.entities.build.RemoteBuildStatus;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.repositories.build.RemoteBuildRepository;
import net.unibld.server.repositories.security.UserProfileRepository;

@Service
public class RemoteBuildServiceImpl implements RemoteBuildService {
	private static final Logger LOG=LoggerFactory.getLogger(RemoteBuildServiceImpl.class);
	
	@Autowired
	private RemoteBuildRepository remoteBuildRepo;
	@Autowired
	private UserProfileRepository profileRepo;
	
	@Override
	@Transactional
	public RemoteBuild startRemoteBuild(String id,Project project, String goal,
			String userId) {
		UserProfile user = profileRepo.findOne(userId);
		if (user==null) {
			throw new IllegalArgumentException("Invalid user id: "+userId);
		}
		RemoteBuild b=new RemoteBuild();
		b.setId(id);
		b.setProject(project);
		b.setGoal(goal);
		b.setUser(user);
		return remoteBuildRepo.save(b);
	}

	@Override
	@Transactional
	public RemoteBuild remoteBuildFailed(String buildId) {
		LOG.info("Remote build failed: {}",buildId);
		RemoteBuild rb = remoteBuildRepo.findByBuildId(buildId);
		if (rb==null) {
			throw new IllegalArgumentException("Invalid build id: "+buildId);
		}
		rb.setCompletedDate(new Date());
		rb.setStatus(RemoteBuildStatus.FAILED);
		return remoteBuildRepo.save(rb);
	}

	@Override
	@Transactional
	public RemoteBuild remoteBuildCompleted(String buildId) {
		LOG.info("Remote build completed: {}",buildId);
		RemoteBuild rb = remoteBuildRepo.findByBuildId(buildId);
		if (rb==null) {
			throw new IllegalArgumentException("Invalid build id: "+buildId);
		}
		rb.setCompletedDate(new Date());
		rb.setStatus(RemoteBuildStatus.COMPLETED);
		return remoteBuildRepo.save(rb);
	}

	@Override
	@Transactional
	public RemoteBuild remoteBuildStarted(String remoteBuildId, Build build) {
		LOG.info("Remote build {} started with build id: {}",remoteBuildId,build.getId());
		RemoteBuild rb = remoteBuildRepo.findOne(remoteBuildId);
		if (rb==null) {
			throw new IllegalArgumentException("Invalid remote build id: "+remoteBuildId);
		}
		rb.setBuild(build);
		return remoteBuildRepo.save(rb);
	}

	@Override
	public RemoteBuild findRemoteBuild(String id) {
		return remoteBuildRepo.findOne(id);
	}

}
