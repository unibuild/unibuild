package net.unibld.server.repositories.security;

import java.util.List;

import net.unibld.core.persistence.model.Project;
import net.unibld.server.entities.security.ProjectUserRight;
import net.unibld.server.entities.security.UserProfile;

import org.springframework.data.repository.CrudRepository;

public interface ProjectUserRightRepository extends CrudRepository<ProjectUserRight, Long> {
	List<ProjectUserRight> findByUser(UserProfile user);
	ProjectUserRight findByUserAndProject(UserProfile user,Project project);

	
}