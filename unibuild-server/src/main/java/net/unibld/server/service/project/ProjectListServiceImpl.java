package net.unibld.server.service.project;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.unibld.core.persistence.model.Project;
import net.unibld.core.repositories.ProjectRepository;
import net.unibld.server.service.query.AbstractListQueryExecutor;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;

@Service("projectListService")
public class ProjectListServiceImpl extends AbstractListQueryExecutor<Project> implements ProjectListService {
	private static final Logger LOG=LoggerFactory.getLogger(ProjectListServiceImpl.class);
	
	@Autowired
	private ProjectRepository projectRepo;

	
	
	@PersistenceContext
	private EntityManager em;
	
	
	@Override
	public ServiceResult<Project> getList(ListQueryInput input) {
		return new ServiceResult<Project>(getResultList(input,Project.class), getTotalCount(input,Project.class));
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}


	protected String getDefaultOrderColumnName() {
		return "name";
	}

	@Override
	public Project findProject(String id) {
		return projectRepo.findOne(id);
	}


	


	
}
