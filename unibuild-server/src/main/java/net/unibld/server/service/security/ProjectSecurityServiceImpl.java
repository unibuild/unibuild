package net.unibld.server.service.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.persistence.model.ProjectAccessLevel;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.ProjectUserRight;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.repositories.security.ProjectUserRightRepository;
import net.unibld.server.repositories.security.UserProfileRepository;
import net.unibld.server.service.project.WorkspaceService;

@Service("projectSecurityService")
public class ProjectSecurityServiceImpl implements ProjectSecurityService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectSecurityServiceImpl.class);
    
	@Autowired
	private ProjectUserRightRepository projectRightRepo;
    
	@Autowired
	private UserProfileRepository profileRepo;
	
	@Autowired
	private WorkspaceService workspaceService;
	
	@Autowired
	private SecurityService securityService;
	
	public List<ProjectUserRight> getUserRights(UserProfile user) {
		return projectRightRepo.findByUser(user);
	}

	@Override
	@Transactional
	public void saveUserRights(UserProfile user,List<ProjectUserRight> rights) {
		if (user==null) {
			throw new IllegalArgumentException("User was null");
		}
		if (rights==null) {
			throw new IllegalArgumentException("User rights was null");
		}
		LOGGER.info("Saving user rights for: {}...",user.getUserName());
		for (ProjectUserRight r:rights) {
			if (r.getUser()==null) {
				r.setUser(user);
			}
			projectRightRepo.save(r);
		}
		
		
	}

	@Override
	public boolean checkProjectUserAccess(Project project, String goal, String userId) {
		UserProfile user = profileRepo.findOne(userId);
		if (user==null) {
			throw new IllegalStateException("Invalid user id: "+userId);
		}
		
		if (securityService.hasRole(userId, Group.ROLE_ADMIN)) {
			return true;
		}
		
		ProjectUserRight right = projectRightRepo.findByUserAndProject(user, project);
		if (right==null || right.getAccessLevel()==ProjectAccessLevel.none) {
			return false;
		}
		
		ProjectConfig conf = workspaceService.loadProjectConfig(project);
		if (conf.getGoalsConfig()!=null && conf.getGoalsConfig().getGoals()!=null) {
			for (BuildGoalConfig c:conf.getGoalsConfig().getGoals()) {
				if (c.getName().equals(goal)) {
					return (c.getAccessLevel()==null || isAccessLevelHigherOrEqual(right.getAccessLevel(),c.getAccessLevel()));
				}
			}
		}
		return false;
	}

	public boolean isAccessLevelHigherOrEqual(ProjectAccessLevel l1, ProjectAccessLevel l2) {
		switch (l1) {
			case contributor:
				return l2==ProjectAccessLevel.contributor;
			case lead:
				return l2==ProjectAccessLevel.contributor||l2==ProjectAccessLevel.lead;
			case owner:
				return true;
			default:
				return false;
		}
	}

	@Override
	public ProjectUserRight getUserProjectRights(UserProfile user, Project project) {
		return projectRightRepo.findByUserAndProject(user, project);
	}
}
