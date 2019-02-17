package net.unibld.core.build;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.unibld.core.test.UnitTestContext;

public class ProjectFileTestHelper {

	static String getProjectFileNotExisting() throws URISyntaxException {
		return ProjectFileTestHelper.getBuildProjectDir()+"/project-notexisting.xml";
	}

	static String getProjectFileExisting() throws URISyntaxException, IOException {
		return UnitTestContext.extractResource(ProjectFileTestHelper.class,"/project-bootstrap.xml");
		
		
	}
	
	static String getProjectFile(String path) throws URISyntaxException, IOException {
		return UnitTestContext.extractResource(ProjectFileTestHelper.class,path);
		
		
	}

	static String getProjectFileExistingEmpty() throws URISyntaxException, IOException {
		return UnitTestContext.extractResource(ProjectFileTestHelper.class,"/project-bootstrap-empty.xml");
		
	}

	static String getBuildProjectDir() {
		File f = new File(UnitTestContext.getBaseDir()+"/bootstrap");
		f.mkdirs();
		return f.getAbsolutePath();
	}

}
