package net.unibld.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import net.unibld.core.persistence.model.BuildTestResult;
import net.unibld.core.persistence.model.BuildTestSuite;

/**
 * A Spring Data repository interface for the entity {@link BuildTestResult}.
 * @author andor
 *
 */
public interface BuildTestResultRepository extends CrudRepository<BuildTestResult, String>{

	List<BuildTestResult> findBySuiteOrderByCreateDateAsc(BuildTestSuite suite);
	
}
