package net.unibld.core.repositories;

import java.util.List;

import net.unibld.core.persistence.model.Project;

import org.springframework.data.repository.CrudRepository;

/**
 * A Spring Data repository interface for the entity {@link Project}.
 * @author andor
 *
 */
public interface ProjectRepository extends CrudRepository<Project, String>{

	/**
	 * Returns all {@link Project} records ordered by their creation dates ascending
	 * @return All {@link Project} records 
	 */
	List<Project> findAllByOrderByCreateDate();

	/**
	 * Returns a {@link Project} record identified by its project file path
	 * @param path Project file path
	 * @return Project record found or null
	 */
	Project findByPath(String path);

	/**
	 * Returns a {@link Project} record identified by its name
	 * @param projectName Project name
	 * @return Project record found or null
	 */
	Project findByName(String projectName);

	/**
	 * Returns all {@link Project} records ordered by their names ascending
	 * @return All {@link Project} records 
	 */
	List<Project> findAllByOrderByName();

}
