package net.unibld.core.repositories;

import org.springframework.data.repository.CrudRepository;

import net.unibld.core.persistence.model.BuildTestResult;

/**
 * A Spring Data repository interface for the entity {@link BuildTestResult}.
 * @author andor
 *
 */
public interface BuildTestResultRepository extends CrudRepository<BuildTestResult, String>{
	
}
