package net.unibld.server.service.security;

import java.util.List;

import net.unibld.core.persistence.model.Project;
import net.unibld.core.persistence.model.ProjectAccessLevel;
import net.unibld.server.entities.security.ProjectUserRight;
import net.unibld.server.entities.security.UserProfile;

public interface ProjectSecurityService {
	List<ProjectUserRight> getUserRights(UserProfile user);
	
	ProjectUserRight getUserProjectRights(UserProfile user,Project project);

	void saveUserRights(UserProfile user, List<ProjectUserRight> rights);

	boolean checkProjectUserAccess(Project project, String goal, String userId);

	boolean isAccessLevelHigherOrEqual(ProjectAccessLevel l1, ProjectAccessLevel l2);
}
