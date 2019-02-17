package net.unibld.server.service.build;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.repositories.BuildRepository;
import net.unibld.server.entities.build.BuildLog;
import net.unibld.server.repositories.build.BuildLogRepository;

@Service
public class BuildLogServiceImpl implements BuildLogService {
	@Autowired
	private BuildLogRepository logRepo;
	
	@Override
	@Transactional
	public BuildLog saveBuildLog(BuildLog log) {
		return logRepo.save(log);
	}

	@Override
	public List<BuildLog> getBuildLogs(String buildId) {
		return logRepo.findByBuildIdOrderByDateAsc(buildId);
	}
	
	
}
