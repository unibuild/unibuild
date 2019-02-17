package net.unibld.server.service.project;

import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Project;

public interface WorkspaceService {
	void openProject(String path);

	ProjectConfig loadProjectConfig(Project project);
	void deleteProject(Project project);
}
