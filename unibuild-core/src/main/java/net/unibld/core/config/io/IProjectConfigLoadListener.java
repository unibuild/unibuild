package net.unibld.core.config.io;

import net.unibld.core.config.ProjectConfig;

/**
 * An interface for project config load success events.
 * @author andor
 *
 */
public interface IProjectConfigLoadListener {

	/**
	 * Event method invoked when a {@link ProjectConfig} has been loaded
	 * @param ret The configuration loaded
	 */
	void projectConfigLoaded(ProjectConfig ret);

}
