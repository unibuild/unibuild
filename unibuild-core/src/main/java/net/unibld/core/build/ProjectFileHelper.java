package net.unibld.core.build;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectFileHelper {
	private static final Logger LOG=LoggerFactory.getLogger(ProjectFileHelper.class);
	
	public static void setupContext(
			BuildToolContext context,String projectDir,String projectFile) {
		if (projectFile!=null) {
			File file = new File(projectFile);
			
			context.setProjectDir(projectDir!=null?projectDir:file.getParentFile().getAbsolutePath());
			context.setProjectFile(file.getAbsolutePath());
			LOG.info("Project dir set according to the parent dir of the first program arg: {}",new File(context.getProjectDir()).getAbsolutePath());
		
		} else {
			if (projectDir!=null) {
				context.setProjectDir(projectDir);
				LOG.info("Project dir set according to the -d/--dir JVM parameter: {}",new File(context.getProjectDir()).getAbsolutePath());
			} else {
	 			context.setProjectDir(".");
	 			LOG.info("Project dir set to the current one: {}",new File(context.getProjectDir()).getAbsolutePath());
			}
			
			context.setProjectFile(FilenameUtils.concat(context.getProjectDir(), "project.xml"));
			
		}
	}

	public static String getDefaultBuildDir(String projectDir) {
		return FilenameUtils.concat(projectDir, "build");
	}
}
