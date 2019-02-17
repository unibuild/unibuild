package net.unibld.server.repositories.build;



import java.util.List;

import org.springframework.data.repository.CrudRepository;

import net.unibld.server.entities.build.BuildLog;

public interface BuildLogRepository extends CrudRepository<BuildLog, Long> {

	List<BuildLog> findByBuildIdOrderByDateAsc(String buildId);
}