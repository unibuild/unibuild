package net.unibld.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.BuildTestSuite;

/**
 * A Spring Data repository interface for the entity {@link BuildTestSuite}.
 * @author andor
 *
 */
public interface BuildTestSuiteRepository extends CrudRepository<BuildTestSuite, String>{

	List<BuildTestSuite> findByBuildOrderByCreateDateAsc(Build build);
	
}
