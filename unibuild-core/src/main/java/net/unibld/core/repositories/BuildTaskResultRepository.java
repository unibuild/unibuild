package net.unibld.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.BuildTaskResult;

/**
 * A Spring Data repository interface for the entity {@link BuildTaskResult}.
 * @author andor
 *
 */
public interface BuildTaskResultRepository extends CrudRepository<BuildTaskResult, String>{

	List<BuildTaskResult> findByBuildOrderByStartDateAsc(Build build);
	
}
