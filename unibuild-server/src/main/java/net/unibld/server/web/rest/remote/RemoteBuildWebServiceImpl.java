package net.unibld.server.web.rest.remote;
 
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import net.unibld.core.persistence.model.Project;
import net.unibld.core.service.ProjectService;
import net.unibld.server.entities.build.RemoteBuild;
import net.unibld.server.entities.build.RemoteBuildStatus;
import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.ProjectUserRight;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.service.build.BuildQueue;
import net.unibld.server.service.build.BuildQueueRequest;
import net.unibld.server.service.build.RemoteBuildListenerFactory;
import net.unibld.server.service.build.RemoteBuildService;
import net.unibld.server.service.security.ProjectSecurityService;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.web.jsf.DatatableLogger;
import net.unibld.server.web.rest.BaseWebService;
import net.unibld.server.web.rest.RestSecurityException;
import net.unibld.server.web.rest.remote.extract.UnzipExtractor;
import net.unibld.server.web.rest.remote.model.BuildStartedDto;
import net.unibld.server.web.rest.remote.model.BuildState;


 
/**
 * An MVC-based web service for running builds using a remote access. 
 * @author andor
 * @see ISecurityService
 */
@Controller
public class RemoteBuildWebServiceImpl extends BaseWebService implements IRemoteBuildWebService {
	private static final Logger LOG=LoggerFactory.getLogger(RemoteBuildWebServiceImpl.class);
	
	
	@Autowired
	private RemoteBuildService remoteBuildService;
	@Autowired
	private BuildQueue buildQueue;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectSecurityService projectSecurityService;
	@Autowired
	private SecurityService securityService;
	


	@Autowired
	private UnzipExtractor unzipExtractor;
	
	@Autowired
	private RemoteBuildListenerFactory remoteBuildListenerFactory;
	
	@RequestMapping(value="/remote/download",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadOutput(HttpServletRequest request,
        HttpServletResponse response,
        @RequestParam String ticket,@RequestParam String id,@RequestParam(required=false) String fileName) 
        		throws IOException  {
	
		
		String userId=doAuth(ticket);
		
		
        if (fileName==null) {
        	fileName="output.zip";
        }
		
        
        RemoteBuild rb=remoteBuildService.findRemoteBuild(id);
		if (rb==null) {
			throw new IllegalArgumentException("Invalid remote build id");
		}
		
		if (!hasProjectRights(userId,rb.getProject())) {
	    	throw new RestSecurityException("Access denied for project: "+rb.getProject().getPath());
	    }
		
		
        if (rb.getStatus()!=RemoteBuildStatus.COMPLETED)
        {
            throw new IllegalStateException(String.format("Build is not completed: %s, id=%s, file=%s", rb.getProject(), id, fileName));
        }
        
        File projDir=new File(rb.getProject().getPath()).getParentFile();
        String buildDirPath=FilenameUtils.concat(projDir.getAbsolutePath(),"build");
	
        String path = FilenameUtils.concat(buildDirPath, fileName);
        File f=new File(path);
        if (!f.exists())
        {
            throw new IllegalArgumentException(String.format("Invalid file: %s, id=%s, file=%s, path=%s", rb.getProject().getName(), id, fileName,path));
        }
		
		
		try {
			response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition",String.format("attachment; filename=\"%s\"",f.getName()));
	 
	        IOUtils.write(FileUtils.readFileToByteArray(f), response.getOutputStream());
		} catch (Exception ex) {
			LOG.error("Failed to read audio file",ex);
		} 
        return null;
 
    }

	
	@RequestMapping(value="/remote/state",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody BuildState getBuildState(@RequestParam String ticket,@RequestParam String id) throws IOException {
		String userId=doAuth(ticket);
		
		RemoteBuild rb=remoteBuildService.findRemoteBuild(id);
		if (rb==null) {
			throw new IllegalArgumentException("Invalid remote build id");
		}
		if (!hasProjectRights(userId,rb.getProject())) {
	    	throw new RestSecurityException("Access denied for project: "+rb.getProject().getPath());
	    }
		BuildState ret=new BuildState();
		ret.setId(id);
		ret.setProject(rb.getProject().getName());
		ret.setCompleted(rb.getStatus()==RemoteBuildStatus.COMPLETED);
		ret.setRunning(rb.getStatus()==RemoteBuildStatus.STARTED);
		ret.setError(rb.getErrorMessage());
		return ret;
	}

	@RequestMapping(value = "/remote/upload", method = RequestMethod.POST)
    public @ResponseBody BuildStartedDto uploadBuildFile(
    		@RequestParam("file") MultipartFile file,
    		@RequestParam(required=true) String ticket,
    		@RequestParam(required=true) String path,
    		@RequestParam(required=true) String goal) throws IOException {
		
		String userId=doAuth(ticket);
		BuildStartedDto result =null;
		String guid=UUID.randomUUID().toString();
		
		Project p = projectService.getProjectByPath(path);
	    if (p==null) {
	       	LOG.error("Invalid project path: {}",path);
	        result=new BuildStartedDto();
	        result.setErrorMessage("ERROR: Invalid project path: "+path);
	        result.setStarted(false);
	        result.setId(guid);
	        return result;
	    }
	        
	    if (!hasProjectRights(userId,p)) {
	    	result=new BuildStartedDto();
	        result.setErrorMessage("ERROR: Access denied for project: "+path);
	        result.setStarted(false);
	        result.setId(guid);
	        return result;
	    }
		
		
        if (!file.isEmpty()) {
        	result = startBuild(guid,p,goal,file,userId);
           
       } else {
    	   LOG.error("Empty file for: project={}",path);
           result=new BuildStartedDto();
           result.setErrorMessage("ERROR: Empty file");
           result.setStarted(false);
          
           
       }
	  
	   return result;
    }



	private boolean hasProjectRights(String userId, Project p) {
		UserProfile user=securityService.findUserProfile(userId);
		if (user==null) {
			return false;
		}
		
		List<Authority> authorities = securityService.getAuthorities(userId);
		for (Authority a:authorities) {
			if (a.getAuthority().equals(Group.ROLE_ADMIN.name())||a.getAuthority().equals(Group.ROLE_SUPER_USER.name())) {
				return true;
			}
		}
		List<ProjectUserRight> rights = projectSecurityService.getUserRights(user);
		for (ProjectUserRight r:rights) {
			if (r.getProject().getId().equals(p.getId())) {
				return true;
			}
		}
		return false;
	}


	private BuildStartedDto startBuild(String guid,Project project, String goal,MultipartFile file,String userId) throws IOException {
		//start the build itself
		RemoteBuild rb = remoteBuildService.startRemoteBuild(guid,project, goal, userId);
		BuildStartedDto ret=new BuildStartedDto();
        ret.setStarted(true);
        ret.setStartTime(rb.getCreateDate());
        ret.setId(rb.getId());
        

        File dir=new File(project.getPath()).getParentFile();
        File tmpDir=new File(FilenameUtils.concat(dir.getAbsolutePath(), "tmp"));
        File resourceDir=new File(FilenameUtils.concat(dir.getAbsolutePath(), "build/resources"));
        
        tmpDir.mkdirs();
        resourceDir.mkdirs();
        
        File resourceZip=new File(FilenameUtils.concat(tmpDir.getAbsolutePath(), "resources.zip"));
        
        FileUtils.writeByteArrayToFile(resourceZip, file.getBytes());
        
        unzipExtractor.extract(resourceZip.getAbsolutePath(), resourceDir.getAbsolutePath());
        LOG.info("Extracted resource file {} to: {}",resourceZip.getAbsolutePath(),
        		resourceDir.getAbsolutePath());
      
        BuildQueueRequest req=new BuildQueueRequest().withProject(project).
				withGoal(goal).
				withListener(remoteBuildListenerFactory.createListener(rb)).
				withUserId(userId);
        
        buildQueue.addToQueue(req);
        return ret;
	}

}