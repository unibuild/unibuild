package net.unibld.server.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

import net.unibld.core.persistence.model.Project;

public class ProjectMapper extends AbstractMapper<Project, ProjectData> {
	

	protected void fillDto(ProjectData dto, Project e) {
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setBuildNumber(e.getBuildNumber());
		dto.setDisplayName(e.getDisplayName());
		dto.setPath(e.getPath());
		dto.setInstallDate(e.getCreateDate());
	}

	@Override
	protected ProjectData createDto() {
		return new ProjectData();
	}
	
}
