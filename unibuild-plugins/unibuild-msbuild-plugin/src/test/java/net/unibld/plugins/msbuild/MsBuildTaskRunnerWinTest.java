package net.unibld.plugins.msbuild;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.build.BuildConstants;
import net.unibld.core.config.TaskConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.plugins.msbuild.MsBuildTask;
import net.unibld.plugins.msbuild.MsBuildTaskRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/msbuild-test-context.xml"})
public class MsBuildTaskRunnerWinTest {
	@Autowired
	private MsBuildTaskRunner runner;
	
	@Test
	@Ignore
	public void testRunMsBuild() throws IOException {
		
		File baseDir = new File("C:/projects/parkl-workspace-dev/client/Parkl.SkiData/Parkl.SkiData.HostComm");
		
		execute(baseDir.getAbsolutePath(),"Parkl.SkiData.HostComm.csproj","Clean;Compile","VisualStudioVersion=14.0");
	}
	
	private void execute(String baseDir,String projectFile,String target,String parameters) throws IOException {
		File frameworkDir = new File("C:/Windows/Microsoft.NET/Framework64/v4.0.30319");
		
		MsBuildTask t=new MsBuildTask();
		t.setBaseDirectory(baseDir);
		t.setProject(projectFile);
		t.setTarget(target);
		t.setFrameworkDir(frameworkDir.getAbsolutePath());
		t.setParameters(parameters);
		
		t.setTaskConfig(new TaskConfig());
		t.getTaskConfig().setTaskType("msbuild");
		t.getTaskConfig().setTaskContext(new TaskContext());
		t.getTaskConfig().getTaskContext().addAttribute(BuildConstants.VARIABLE_NAME_BUILD_DIR, "./target");
		
		runner.run(t);
	}
	
	
}
