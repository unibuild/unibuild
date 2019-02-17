package net.unibld.core.build;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import net.unibld.core.BuildProject;
import net.unibld.core.BuildTask;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.service.BuildService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests basic build persistence operations against a JPA datasource
 * @author andor
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class JpaBuildPersistenceTest {
	private static final Logger LOG=org.slf4j.LoggerFactory.getLogger(JpaBuildPersistenceTest.class);
	
	@Autowired
	private BuildService persistence;
	
	@Autowired
	private ProjectLoader projectLoader;
	
	
	@Autowired
	private BuildToolContextHolder contextHolder;
	
	
	@Test
	public void testBuildPersistenceFirstTime() throws URISyntaxException, IOException {
		BuildProject p=new BuildProject();
		p.setName("test-project");
		String path = ProjectFileTestHelper.getProjectFileExisting();
		
		
		
		String id=UUID.randomUUID().toString();
		BuildToolContext c=contextHolder.createContext(id);
		
		ProjectConfig pcfg = projectLoader.loadProject(c,path,new String[0]);
		p.setProjectConfig(pcfg);
		Build b = persistence.startBuild(id,p, path, "build", "unibld");
		Assert.assertNotNull(b);
		
		persistence.buildCompleted(id);
		
		b=persistence.findBuild(id);
		Assert.assertNotNull(b);
		Assert.assertNotNull(b.getId());
		Assert.assertEquals(id,b.getId());
		Assert.assertTrue(b.isSuccessful());
		Assert.assertNotNull(b.getCompleteDate());
		Assert.assertEquals("unibld",b.getCreatorUser());
	}
	
	@Test
	public void testBuildPersistenceFailed() throws URISyntaxException, IOException {
		BuildProject p=new BuildProject();
		p.setName("test-project");
		String path = ProjectFileTestHelper.getProjectFileExisting();
		
		String id=UUID.randomUUID().toString();
		
		BuildToolContext c=contextHolder.createContext(id);
		ProjectConfig pcfg = projectLoader.loadProject(c,path,new String[0]);
		
		p.setProjectConfig(pcfg);
		Build b = persistence.startBuild(id, p, path, "build", "unibld");
		Assert.assertNotNull(b);
		
		BuildTask firstTask = pcfg.getGoalsConfig().getGoals().get(0).getTasks().getTasks().get(0);
		Assert.assertNotNull(firstTask);
		persistence.buildFailed(id,firstTask.getClass().getName(),0,new BuildException("Test error"),null);
		
		b=persistence.findBuild(id);
		Assert.assertNotNull(b);
		Assert.assertNotNull(b.getId());
		Assert.assertEquals(id,b.getId());
		Assert.assertFalse(b.isSuccessful());
		Assert.assertNotNull(b.getCompleteDate());
		Assert.assertEquals("unibld",b.getCreatorUser());
		Assert.assertEquals(b.getErrorMessage(), "Test error");
		Assert.assertEquals(b.getFailedTaskClass(),firstTask.getClass().getName());
		Assert.assertEquals(b.getFailedTaskIndex().intValue(),0);
	}
}
