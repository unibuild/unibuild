package net.unibld.server.web.rest.remote.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="buildDownloadRequest")
@XmlAccessorType(XmlAccessType.FIELD)
    public class BuildDownloadRequestDto
    {
    	@XmlAttribute
    	private String project;
    	@XmlAttribute
    	private String buildFileName;
		public String getProject() {
			return project;
		}
		public void setProject(String project) {
			this.project = project;
		}
		public String getBuildFileName() {
			return buildFileName;
		}
		public void setBuildFileName(String buildFileName) {
			this.buildFileName = buildFileName;
		}
        
    }
