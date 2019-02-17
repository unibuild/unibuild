package net.unibld.plugins.innosetup;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;



/**
 * A task that sets different properties of an InnoSetup installer build in a specified .iss file
 * @author andor
 *
 */
@Task(name="innosetup-edit",runnerClass=InnoSetupEditTaskRunner.class)
public class InnoSetupEditTask extends BuildTask {

	
	private static final long serialVersionUID = -1791732044097739987L;
	private String issPath;
	private String defines;
	private String version;
	private boolean exitIfDefineNotFound=true;
	private String encoding;
	private String pomFile;
	private String buildNumberProperties;
	private String versionDefineName;
	private String buildNumberPropertyName;
	private boolean useProjectVersion=false;
	/**
	 * Default constructor
	 */
	public InnoSetupEditTask(){
		super();
	}
	/**
	 * @return Path of the .iss file to edit
	 */
	public String getIssPath() {
		return issPath;
	}
	/**
	 * @param issPath Path of the .iss file to edit
	 */
	public void setIssPath(String issPath) {
		this.issPath = issPath;
	}
	/**
	 * @return Key-value pair of "#define" lines to edit in a comma-separated list as in [key=value],[key2=value2] etc.
	 */
	public String getDefines() {
		return defines;
	}
	/**
	 * @param defines Key-value pair of "#define" lines to edit in a comma-separated list as in [key=value],[key2=value2] etc.
	 */
	public void setDefines(String defines) {
		this.defines = defines;
	}
	/**
	 * @return Static version value 
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version Static version value 
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return True if the task should exit if defines are not found
	 */
	public boolean isExitIfDefineNotFound() {
		return exitIfDefineNotFound;
	}
	/**
	 * @param exitIfDefineNotFound True if the task should exit if defines are not found
	 */
	public void setExitIfDefineNotFound(boolean exitIfDefineNotFound) {
		this.exitIfDefineNotFound = exitIfDefineNotFound;
	}
	/**
	 * @return Encoding
	 */
	public String getEncoding() {
		return encoding;
	}
	/**
	 * @param encoding Encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	/**
	 * @return Maven pom.xml file to use as input version
	 */
	public String getPomFile() {
		return pomFile;
	}
	/**
	 * @param pomFile Maven pom.xml file to use as input for version
	 */
	public void setPomFile(String pomFile) {
		this.pomFile = pomFile;
	}
	/**
	 * @return Path of buildNumber.properties-style file to use as input for build number (the 4th and last
	 * part of version)
	 */
	public String getBuildNumberProperties() {
		return buildNumberProperties;
	}
	/**
	 * @param buildNumberProperties Path of buildNumber.properties-style file to use as input for build number (the 4th and last
	 * part of version)
	 */
	public void setBuildNumberProperties(String buildNumberProperties) {
		this.buildNumberProperties = buildNumberProperties;
	}
	/**
	 * @return The name of the "#define" line that contains the version
	 */
	public String getVersionDefineName() {
		return versionDefineName;
	}
	/**
	 * @param versionDefineName The name of the "#define" line that contains the version
	 */
	public void setVersionDefineName(String versionDefineName) {
		this.versionDefineName = versionDefineName;
	}
	/**
	 * @return The name of the build number property in the buildNumber.properties file (default is 'buildNumber')
	 */
	public String getBuildNumberPropertyName() {
		return buildNumberPropertyName;
	}
	/**
	 * @param buildNumberPropertyName The name of the build number property in the buildNumber.properties file (default is 'buildNumber')
	 */
	public void setBuildNumberPropertyName(String buildNumberPropertyName) {
		this.buildNumberPropertyName = buildNumberPropertyName;
	}
	public boolean isUseProjectVersion() {
		return useProjectVersion;
	}
	public void setUseProjectVersion(boolean useProjectVersion) {
		this.useProjectVersion = useProjectVersion;
	}
	
}