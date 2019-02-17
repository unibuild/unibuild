package net.unibld.server.repositories.build;



import net.unibld.server.entities.build.RemoteBuild;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RemoteBuildRepository extends CrudRepository<RemoteBuild, String> {

	@Query(value = "SELECT OBJECT(r) FROM RemoteBuild AS r WHERE r.build.id=?1")
	RemoteBuild findByBuildId(String buildId);
}