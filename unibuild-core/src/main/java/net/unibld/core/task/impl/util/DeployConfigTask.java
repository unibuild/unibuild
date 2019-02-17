package net.unibld.core.task.impl.util;

import net.unibld.core.BuildTask;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.task.annotations.Task;



/**
 * Deploys a specified configuration source folder to the path defined in the 'configDir' element of the 
 * {@link ProjectConfig} in the project.xml
 * @author andor
 *
 */
@Task(name="deploy-config",runnerClass=DeployConfigTaskRunner.class)
public class DeployConfigTask extends BuildTask {

	private static final long serialVersionUID = 7348294462590880027L;
	private String source;
	private String configDir;
	
	

	/**
	 * @return Configuration source folder
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source Configuration source folder
	 */
	public void setSource(String source) {
		this.source = source;
	}



	public String getConfigDir() {
		return configDir;
	}



	public void setConfigDir(String configDir) {
		this.configDir = configDir;
	}

	
		
}//end CustomNativeTask