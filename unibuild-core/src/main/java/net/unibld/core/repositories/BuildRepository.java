package net.unibld.core.repositories;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;

import org.springframework.data.repository.CrudRepository;

/**
 * A Spring Data repository interface for the entity {@link Build}.
 * @author andor
 *
 */
public interface BuildRepository extends CrudRepository<Build, String>{
	/**
	 * Returns the last started {@link Build} or null
	 * @return The last started build or null
	 */
	public Build findFirstByOrderByStartDateDesc();

	/**
	 * Deletes {@link Build} records by {@link Project}
	 * @param project Project to delete by
	 */
	public void deleteByProject(Project project);
}
