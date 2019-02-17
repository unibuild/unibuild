package net.unibld.core.service;

import java.util.List;
import java.util.UUID;

import net.unibld.core.BuildProject;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.repositories.BuildRepository;
import net.unibld.core.repositories.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence service for entity {@link Project}, implemented as a Spring {@link Service}. 
 * @author andor
 *
 */
@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectRepository projectRepo;
	@Autowired
	private BuildRepository buildRepo;
	
	@Override
	public List<Project> getAvailableProjects() {
		return projectRepo.findAllByOrderByCreateDate();
	}


	@Override
	public List<Project> getAvailableProjectsOrderByName() {
		return projectRepo.findAllByOrderByName();
	}



	@Override
	public Project findProject(String id) {
		return projectRepo.findOne(id);
	}




	@Override
	@Transactional
	public Project saveProject(BuildProject project, String path, String user) {
		Project p = projectRepo.findByPath(path);
		if (p==null) {
			p=new Project();
			p.setId(UUID.randomUUID().toString());
			p.setName(project.getName());
			p.setPath(path);
			p.setBuildNumber(0);
			p.setCreatorUser(user);
			p.setLastModifierUser(user);
			
		} else {
			project.setId(p.getId());
			p.setBuildNumber(p.getBuildNumber()+1);
			p.setLastModifierUser(user);
		}
		
		p=projectRepo.save(p);
		project.setId(p.getId());
		return p;
	}



	public Project getLastProject() {
		Build b=buildRepo.findFirstByOrderByStartDateDesc();
		if (b==null) {
			return null;
		}
		return projectRepo.findOne(b.getProject().getId());
	}
	public String getLastGoal() {
		Build b=buildRepo.findFirstByOrderByStartDateDesc();
		
		if (b==null) {
			return null;
		}
		return b.getGoal();
	}


	@Transactional
	public void deleteAllProjects() {
		projectRepo.deleteAll();
	}



	@Override
	public Project getProjectByName(String projectName) {
		return projectRepo.findByName(projectName);
	}



	@Override
	public Project getProjectByPath(String path) {
		return projectRepo.findByPath(path);
	}


	@Override
	@Transactional
	public void deleteProject(Project project) {
		if (project==null) {
			throw new IllegalArgumentException("Project was null");
		}
		
		buildRepo.deleteByProject(project);
		projectRepo.delete(project);
	}
}
