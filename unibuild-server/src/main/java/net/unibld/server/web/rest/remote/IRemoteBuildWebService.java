package net.unibld.server.web.rest.remote;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.unibld.server.web.rest.remote.model.BuildStartedDto;
import net.unibld.server.web.rest.remote.model.BuildState;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

public interface IRemoteBuildWebService {
	
	public BuildStartedDto uploadBuildFile(MultipartFile file,String ticket,String path,String goal) throws IOException;
		
	public ModelAndView downloadOutput(HttpServletRequest request,
	        HttpServletResponse response,String ticket,String id,String fileName) throws IOException;
	
	public BuildState getBuildState(String ticket,String id) throws IOException;
		
	
}
