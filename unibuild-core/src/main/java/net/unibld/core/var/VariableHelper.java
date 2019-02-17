package net.unibld.core.var;

import java.util.HashMap;
import java.util.Map;

import net.unibld.core.build.BuildException;
import net.unibld.core.build.ParameterMap;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.config.Variable;
import net.unibld.core.config.Variables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for user-defined variables
 * @author andor
 *
 */
public class VariableHelper {
	private static final Logger LOG=LoggerFactory.getLogger(VariableHelper.class);
	
	/**
	 * Initializes user-defined variables based on the {@link Variables} project.xml
	 * config node under {@link ProjectConfig} and command line arguments (user-defined
	 * variables are supposed to be passed as --var)
	 * @param vars Variables config node
	 * @param args All command line args
	 * @return User defined variables as a map
	 * 
	 */
	public static Map<String,String> initVariables(Variables vars, String[] args) {
		Map<String,String> userVars=new HashMap<String, String>();
		if (vars==null||vars.getVariables()==null||vars.getVariables().size()==0) {
			return userVars;
		}
		Map<String,Variable> varMap=new HashMap<String, Variable>();
		for (Variable v:vars.getVariables()) {
			if (varMap.containsKey(v.getName())) {
				throw new BuildException("Duplicate variable: "+v.getName());
			}
			if (ParameterMap.isIllegalName(v.getName())) {
				throw new BuildException("Illegal variable name: "+v.getName());
			}
			varMap.put(v.getName(), v);
			LOG.info("Detected user-defined variable: {} (switch={},default={})",v.getName(),v.isSwitchType(),v.getDefaultValue());
		}
		
		for (String a:args) {
			String arg=a.replace("\"", "").trim();
			LOG.info("Processing argument: {}...",arg);
			if (arg.startsWith("--")) {
				String[] split = arg.substring(2).split("=");
				String name=split[0];
				if (varMap.containsKey(name)) {
					LOG.info("User-defined argument detected: {}",name);
					Variable v = varMap.get(name);
					if (v.isSwitchType()) {
						if (split.length!=1) {
							throw new BuildException(String.format("Invalid argument syntax in '%s': switches cannot have values",arg));
						}
						userVars.put(name, "true");
						LOG.info("User-defined switch '{}' specified in args with value: true",name);
					} else {
						if (split.length!=2) {
							throw new BuildException(String.format("Invalid argument syntax in '%s': non-switch-type arguments must have values",arg));
						}
						userVars.put(name, split[1]);
						LOG.info("User-defined var '{}' specified in args with value: {}",name,split[1]);
					}
					varMap.remove(name);
					
				}
			}
		}
		
		for (String key:varMap.keySet()) {
			Variable v = varMap.get(key);
			if (v.isSwitchType()) {
				boolean def=v.getDefaultValue()==null?false:v.getDefaultValue().equals("true");
				LOG.info("Using default value for user-defined switch '{}': {}",v.getName(),def);
				userVars.put(key, String.valueOf(def));
			} else if (v.getDefaultValue()!=null) {
				LOG.info("Using default value for user-defined var '{}': {}",v.getName(),v.getDefaultValue());
				userVars.put(key, v.getDefaultValue());
			}
		}
		return userVars;
	}
	
	

}
