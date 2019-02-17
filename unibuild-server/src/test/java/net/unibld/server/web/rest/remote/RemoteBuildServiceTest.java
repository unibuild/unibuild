package net.unibld.server.web.rest.remote;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.persistence.model.Project;
import net.unibld.core.persistence.model.ProjectAccessLevel;
import net.unibld.core.service.ProjectService;
import net.unibld.server.entities.build.RemoteBuild;
import net.unibld.server.entities.build.RemoteBuildStatus;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.ProjectUserRight;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.UserStatus;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.service.build.RemoteBuildService;
import net.unibld.server.service.project.WorkspaceService;
import net.unibld.server.service.security.ProjectSecurityService;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.service.security.SpringSecurityException;
import net.unibld.server.service.security.ticket.UserTicketService;
import net.unibld.server.web.rest.remote.model.BuildStartedDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContextWsTest.xml"})
public class RemoteBuildServiceTest {
	
	@Autowired
	private ProjectService projectService;
	@Autowired
	private SecurityService securityService;
	@Autowired
	private ProjectSecurityService projectSecurityService;
	@Autowired
	private UserTicketService ticketService;
	@Autowired
	private RemoteBuildService remoteBuildService;
	
	@Autowired
	private WorkspaceService workspaceService;
	@BeforeClass
	public static void init() {
		new File("./target/plugins").mkdirs();
	}

	@Autowired
	private IRemoteBuildWebService remoteBuildWs;
	
	@Test
	public void testRemoteBuild() throws IOException, SpringSecurityException, InterruptedException {
		InputStream is = getClass().getResourceAsStream("/project.xml");
		Assert.assertNotNull(is);
		
		byte[] projBytes = IOUtils.toByteArray(is);
		
		File dir=new File("./target/remotebuild");
		dir.mkdirs();
		
		File projFile=new File(FilenameUtils.concat(dir.getAbsolutePath(), "project.xml"));
		FileUtils.writeByteArrayToFile(projFile, projBytes);
		
		
		InputStream is2 = getClass().getResourceAsStream("/resources.zip");
		Assert.assertNotNull(is2);
		
		byte[] resBytes = IOUtils.toByteArray(is2);
		
		
		File resFile=new File("./target/resources.zip");
		FileUtils.writeByteArrayToFile(resFile, resBytes);
		
		//project init
		
		Project project=projectService.getProjectByPath(projFile.getAbsolutePath());
		if (project==null) {
			workspaceService.openProject(projFile.getAbsolutePath());
			project=projectService.getProjectByPath(projFile.getAbsolutePath());
			
		}
		Assert.assertNotNull(project);
		
		//user init
		UserProfile user=null;
		if (!securityService.isUserExisting("test@unibld.net")) {
			user=new UserProfile();
			user.setActivated(true);
			user.setUserName("test@unibld.net");
			user.setEmail("test@unibld.net");
			user.setStatus(UserStatus.REGISTERED);
			user.setFirstName("UniBuild");
			user.setLastName("Administrator");
			user=securityService.saveUser(user, Group.ROLE_USER, "test123");
		} else {
			user=securityService.findUserProfile("test@unibld.net");
		}
		
		ProjectUserRight r=new ProjectUserRight();
		r.setAccessLevel(ProjectAccessLevel.owner);
		r.setProject(project);
		r.setUser(user);
		projectSecurityService.saveUserRights(user, Arrays.asList(r));
		
		
		UserTicket t = ticketService.createAccessTicket(user.getUserName());
		Assert.assertNotNull(t);
		
		MockMultipartFile file = new MockMultipartFile("file", "resources.zip", "application/zip", resBytes);
        
		BuildStartedDto dto = remoteBuildWs.uploadBuildFile(file, t.getId(), projFile.getAbsolutePath(), "build");
		Assert.assertNotNull(dto);
		
		boolean running=true;
		while (running) {
			Thread.sleep(1000);
			RemoteBuild rb = remoteBuildService.findRemoteBuild(dto.getId());
			Assert.assertNotNull(rb);
			running = rb.getStatus()==RemoteBuildStatus.STARTED;
		}
		
		RemoteBuild rb = remoteBuildService.findRemoteBuild(dto.getId());
		Assert.assertTrue(rb.getStatus()==RemoteBuildStatus.COMPLETED);
	}
}
