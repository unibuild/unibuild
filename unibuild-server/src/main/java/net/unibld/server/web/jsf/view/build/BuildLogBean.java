package net.unibld.server.web.jsf.view.build;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * A managed bean to show a specific log file of a build run. Log file path is passed in a
 * Base-64 encoded request parameter
 * @author andor
 *
 */
@ManagedBean(name="buildLogBean")
@ViewScoped
public class BuildLogBean {
	private static final Logger LOGGER=LoggerFactory.getLogger(BuildLogBean.class);
	private String log;
	private String logFilePath;
	
	/**
	 * Initializes the bean by checking the request parameter 'log' for a log file specified.
	 */
	@PostConstruct
	public void init() {
		 HttpServletRequest req=(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		 String path=req.getParameter("log");
		 if (!StringUtils.isEmpty(path)) {
			 try {
				loadModel(new String(Base64.decodeBase64(path.trim().getBytes()),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("Unsupported encoding: UTF-8");
			} catch (IOException e) {
				throw new IllegalStateException("Failed to read log file: "+path,e);
			}
		 } else {
			 throw new IllegalStateException("Log file not specified");
		 }
	}
	
	private void loadModel(String path) throws IOException {
		LOGGER.info("Loading log file: {}",path);
		this.logFilePath=path;
		File file=new File(path);
		if (!file.exists()||!file.isFile()) {
			throw new IllegalArgumentException("Log file does not exist: "+path);
		}
		
		log=FileUtils.readFileToString(file, "UTF-8");
	}

	/**
	 * @return Log file content
	 */
	public String getLog() {
		return log;
	}

	/**
	 * @return Log file path
	 */
	public String getLogFilePath() {
		return logFilePath;
	}
	
	
}
