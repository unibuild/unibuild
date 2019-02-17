package net.unibld.server.web.rest.dto;

import net.unibld.core.persistence.model.Build;

public class BuildMapper extends AbstractMapper<Build, BuildData>{

	@Override
	protected void fillDto(BuildData dto, Build e) {
		dto.setBuildDir(e.getBuildDir());
		dto.setId(e.getId());
		dto.setCompleteDate(e.getCompleteDate());
		dto.setStartDate(e.getStartDate());
		dto.setErrorMessage(e.getErrorMessage());
		dto.setGoal(e.getGoal());
		dto.setProjectId(e.getProject().getId());
		dto.setProjectName(e.getProject().getName());
		dto.setUser(e.getCreatorUser());
		dto.setSuccessful(e.isSuccessful());
		dto.setTaskCount(e.getTaskCount());
		dto.setFailedTaskIndex(e.getFailedTaskIndex());
	}

	@Override
	protected BuildData createDto() {
		return new BuildData();
	}

}
