package net.unibld.server.web.rest.remote.model;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.web.multipart.MultipartFile;


@XmlRootElement(name="buildRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildRequestDto implements Serializable
{
	@XmlAttribute
    private String project;
	@XmlAttribute
    private String buildFileName;
	@XmlAttribute
    private String buildType;
	@XmlAttribute
    private String outputDir;
	
	@XmlTransient
	private MultipartFile file;
	@XmlTransient
	private byte[] fileContent;

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

	public String getBuildType() {
		return buildType;
	}

	public void setBuildType(String buildType) {
		this.buildType = buildType;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	
	
}

