package net.unibld.server.service.build;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.service.BuildService;
import net.unibld.server.entities.build.RemoteBuild;
import net.unibld.server.entities.build.RemoteBuildStatus;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.repositories.build.RemoteBuildRepository;
import net.unibld.server.repositories.security.UserProfileRepository;
import net.unibld.server.service.query.AbstractListQueryExecutor;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service class that exposes functionality related to the entity {@link Build}, that represents a single
 * build run in the system
 * @author andor
 *
 */
@Service("buildListService")
public class BuildListServiceImpl extends AbstractListQueryExecutor<Build> implements BuildListService {
	
	@Autowired
	private BuildService buildPersistence;
	
	
	
	@Override
	public Build findBuild(String id) {
		return buildPersistence.findBuild(id);
	}
	
	@PersistenceContext
	private EntityManager em;
	
	
	@Override
	public ServiceResult<Build> getList(ListQueryInput input) {
		return new ServiceResult<Build>(getResultList(input,Build.class), getTotalCount(input,Build.class));
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}


	protected String getDefaultOrderColumnName() {
		return "startDate";
	}

	@Override
	protected boolean isDefaultOrderAscending() {
		return false;
	}

	@Override
	protected Predicate createWherePredicate(CriteriaBuilder cb,
			Root<Build> root, String key, Object val) {
		if (key.equals("project.name")) {
			Path<String> namePath=root.get("project").get("name");
			return cb.like(cb.lower(namePath), "%"+((String)val).toLowerCase()+"%");
		}
		
		if (key.equals("buildNumber")) {
			Path<String> numPath=root.get("buildNumber");
			try {
				return cb.equal(numPath, Integer.parseInt((String)val));
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		return super.createWherePredicate(cb, root, key, val);
	}

	
}
