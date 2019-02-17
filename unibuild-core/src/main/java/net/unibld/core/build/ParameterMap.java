package net.unibld.core.build;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that contains a named set of predefined basic parameters and 
 * a hash map of custom parameters.
 * @author andor
 *
 */
public class ParameterMap {
	
	
	private static final String OPT_CONFIGDIR_LONG = "--config-dir";

	private static final String OPT_FILE_LONG = "--file";

	private static final String OPT_DIR_LONG = "--dir";

	private static final String OPT_INFO_LONG = "--info";

	private static final String OPT_QUIET_LONG = "--quiet";

	private static final String OPT_VERBOSE_LONG = "--verbose";

	private static final String OPT_TRACE_LONG = "--trace";

	private Map<String,String> userVars=new HashMap<>();
	
	private boolean verbose=false;
	private boolean quiet=false;
	private boolean trace=false;
	private String goal=null;
	
	private String projectFile;

	private String projectDir;

	private boolean info;

	private String configDir;
	

	/**
	 * @return True if output is verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}
	
	/**
	 * @return True if output is quiet
	 */
	public boolean isQuiet() {
		return quiet;
	}
	/**
	 * @return True if trace mode is enabled
	 */
	public boolean isTrace() {
		return trace;
	}
	/**
	 * @return The selected goal or null
	 */
	public String getGoal() {
		return goal;
	}
	
	
	
	/**
	 * @return Project file (-f or --file), defaults to project.xml if null
	 */
	public String getProjectFile() {
		return projectFile;
	}
	
	/**
	 * Initializes basic parameter values from the specified command 
	 * line arguments
	 * @param args Command line arguments
	 */
	public void initBasicParameters(String[] args) {
		for (int i=0;i<args.length;i++) {
			String arg=args[i].replace("\"", "").trim();
			
			if (arg.equals("-t")||arg.equals(OPT_TRACE_LONG)) {
				trace=true;
			}
			else if (arg.equals("-v")||arg.equals(OPT_VERBOSE_LONG)) {
				verbose=true;
			}
			else if (arg.equals("-q")||arg.equals(OPT_QUIET_LONG)) {
				quiet=true;
			}
			else if (arg.equals(OPT_INFO_LONG)) {
				info=true;
			}
			if (!arg.startsWith("-")) {
				goal=arg;
				break;
			} else if (arg.equals("-d")||arg.equals(OPT_DIR_LONG)) {
				projectDir=args[i+1].replace("\"", "").trim();
				i++;
			} else if (arg.equals("-f")||arg.equals(OPT_FILE_LONG)) {
				projectFile=args[i+1].replace("\"", "").trim();
				i++;
			} else if (arg.equals("-c")||arg.equals(OPT_CONFIGDIR_LONG)) {
				configDir=args[i+1].replace("\"", "").trim();
				i++;
			} 
		}
		
		
		if (projectDir==null) {
			projectDir = System.getProperty(BuildConstants.VARIABLE_NAME_PROJECT_DIR);
		}
		
		
		
		
	}
	public String getConfigDir() {
		return configDir;
	}

	/**
	 * @return Project dir parameter (-d)
	 */
	public String getProjectDir() {
		return projectDir;
	}
	
	/**
	 * @return A trace string of the underlying parameters
	 */
	public String traceParameters() {
		//basic
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
		if (quiet) {
			if (b.length()>0) {
				b.append(", ");
			}
			b.append("quiet mode");
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
		if (userVars!=null&&userVars.size()>0) {
			if (b.length()>0) {
				b.append("\n");
			}
			b.append("User defined: ");
			int i=0;
			for (String key:userVars.keySet()) {
				if (i>0) {
					b.append(", ");
				}
				b.append(key);
				b.append("=");
				b.append(userVars.get(key));
				i++;
			}
 		}
		return b.toString();
	}

	/**
	 * @return True if the info switch is on (--info)
	 */
	public boolean isInfo() {
		return info;
	}


	
	/**
	 * @return Map of user-defined variables by name
	 */
	public Map<String, String> getUserVars() {
		return userVars;
	}

	/**
	 * @param goal Goal to run set externally
	 */
	public void setGoal(String goal) {
		this.goal = goal;
	}

	/**
	 * @param projectFile Project XML file set externally
	 */
	public void setProjectFile(String projectFile) {
		this.projectFile = projectFile;
	}

	/**
	 * @param projectDir Project directory set externally
	 */
	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}

	public static boolean isIllegalName(String name) {
		if (name==null||name.length()==0) {
			return true;
		}
		return (name.equals(OPT_DIR_LONG)||
				name.equals(OPT_FILE_LONG)||name.equals(OPT_INFO_LONG)||
				name.equals(OPT_QUIET_LONG)||
				name.equals(OPT_TRACE_LONG)||name.equals(OPT_VERBOSE_LONG));
		
	}
}
