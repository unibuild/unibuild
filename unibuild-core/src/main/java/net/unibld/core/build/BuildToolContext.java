package net.unibld.core.build;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.unibld.core.log.Verbosity;
import net.unibld.core.util.PlatformHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a global context for running the build tool in any kind of situation.<br>
 * There is a separate {@link BuildToolContext} defined for every single run, identified
 * by a unique id (run id)
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:20
 */
public class BuildToolContext implements IBuildContextAttributeContainer {

	private String id;
	private String projectDir;
	private String projectFile;
	private String buildDir;
	private Map<String, String> variables;

	private static final Logger LOG=LoggerFactory.getLogger(BuildToolContext.class);
	

	/**
	 * Constructor with a unique run id
	 * @param id Run id
	 */
	public BuildToolContext(String id) {
		this.id=id;
	}
	
	/**
	 * @return Project directory or null if current dir should be used
	 */
	public String getProjectDir() {
		return projectDir;
	}

	/**
	 * @param dir Project directory or null if current dir should be used
	 */
	public void setProjectDir(String dir) {
		projectDir=dir;
		if (attributeMap==null) {
			attributeMap=createAttributeMap();
		} else {
			if (projectDir!=null) {
				//making it absolute
				attributeMap.put(BuildConstants.VARIABLE_NAME_PROJECT_DIR, new File(projectDir).getAbsolutePath());
			} else {
				attributeMap.remove(BuildConstants.VARIABLE_NAME_PROJECT_DIR);
			}
		}
	}




	/**
	 * @param buildDir Build directory
	 */
	public void setBuildDir(String buildDir) {
		this.buildDir=buildDir;
		if (attributeMap==null) {
			attributeMap=createAttributeMap();
		} else {
			if (buildDir!=null) {
				//making it absolute
				attributeMap.put(BuildConstants.VARIABLE_NAME_BUILD_DIR, new File(buildDir).getAbsolutePath());
			} else {
				attributeMap.remove(BuildConstants.VARIABLE_NAME_BUILD_DIR);
			}
		}
	}

	/**
	 * @return Build directory
	 */
	public String getBuildDir() {
		return buildDir;
	}

	

	/**
	 * @return Project file or null if project.xml should be used in the current dir
	 */
	public String getProjectFile() {
		return projectFile;
	}

	/**
	 * @param projectFile Project file or null if project.xml should be used in the current dir
	 */
	public void setProjectFile(String projectFile) {
		this.projectFile = projectFile;
		
	}
	

	private Map<String,String> attributeMap=null;
	
	public String getBuildContextAttribute(String name) {
		if (name==null) {
			throw new IllegalArgumentException("Attribute name was null");
		}
		
		if (attributeMap==null) {
			attributeMap=createAttributeMap();
		}
		if (attributeMap.containsKey(name)) {
			return attributeMap.get(name);
		}
		
		if (variables!=null&&variables.containsKey(name)) {
			return variables.get(name);
		}
		return null;
	}
	
	private DateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
	private DateFormat timeFormat=new SimpleDateFormat("yyyyMMdd_HHmmss");

	private Map<String, String> createAttributeMap() {
		Map<String,String> map=new HashMap<>();
		
		if (buildDir!=null) {
			//making it absolute
			map.put(BuildConstants.VARIABLE_NAME_BUILD_DIR, new File(buildDir).getAbsolutePath());
		}
		
		Date date=new Date();
		map.put(BuildConstants.VARIABLE_NAME_BUILD_START_DATE, dateFormat.format(date));
		map.put(BuildConstants.VARIABLE_NAME_BUILD_START_TIME, timeFormat.format(date));
		
		
		if (projectDir!=null) {
			map.put(BuildConstants.VARIABLE_NAME_PROJECT_DIR, projectDir);
		}
		
		String userHome = PlatformHelper.getUserHome();
		if (userHome!=null) {
			map.put(BuildConstants.VARIABLE_NAME_USER_HOME, userHome);
		} else {
			map.put(BuildConstants.VARIABLE_NAME_USER_HOME,PlatformHelper.detectDocumentsFolder());
		}
		
		return map;
	}

	/**
	 * @param userVars Map of user-defined variables
	 */
	public void setVariables(Map<String, String> userVars) {
		variables=userVars;
	}
	
	/**
	 * @return Map of user-defined variables
	 */
	public Map<String, String> getVariables() {
		return variables;
	}

	public Map<String,String> getTaskContextAttributeMap() {
		
		if (attributeMap==null) {
			attributeMap=createAttributeMap();
		}
		Map<String,String> ret=new HashMap<>();
		
		for (String key:attributeMap.keySet()) {
			LOG.debug("Using variable from attribute map: {} = {}",key,attributeMap.get(key));
			
			ret.put(key,attributeMap.get(key));
		}
	
		if (variables!=null) {
			for (String key:variables.keySet()) {
				if (!ret.containsKey(key)) {
					LOG.debug("Using variable from map: {} = {}",key,variables.get(key));
					ret.put(key,variables.get(key));
				}
			}
		}
		return ret;
	}

	/**
	 * @return Unique run id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Creates a trace string of the underlying context 
	 * @param verbosity Verbosity setting
	 * @return A trace string of the underlying parameters
	 */
	public String traceContext(Verbosity verbosity) {
		//basic
		boolean verbose = verbosity!=null&&verbosity.isVerbose();
		boolean trace = verbosity!=null&&verbosity.isTrace();
		StringBuilder b=new StringBuilder();
		if (verbose) {
			
			b.append("verbose mode");
		}
		if (trace) {
			if (b.length()>0) {
				b.append(", ");
			}
			b.append("trace mode");
		}
		
		
		if (projectDir!=null) {
			if (b.length()>0) {
				b.append(", ");
			}
			b.append(String.format("project-dir=\"%s\"",projectDir));
		}
		if (projectFile!=null) {
			if (b.length()>0) {
				b.append("\n");
			}
			b.append(String.format("project-file=\"%s\"",projectFile));
		}
		
		
		//user defined
		if (variables!=null&&variables.size()>0) {
			if (b.length()>0) {
				b.append("\n");
			}
			b.append("Variables: ");
			int i=0;
			for (String key:variables.keySet()) {
				if (i>0) {
					b.append(", ");
				}
				b.append(key);
				b.append("=");
				b.append(variables.get(key));
				i++;
			}
 		}
		return b.toString();
	}


}//end BuildToolContext