package net.unibld.plugins.tomcat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.unibld.core.config.InstanceConfig;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.task.impl.server.ServerTaskRunner;
import net.unibld.core.util.PlatformHelper;

/**
 * A task runner that manipulates a Tomcat installation using shell scripts.
 * @author andor
 *
 */
@Component
public class TomcatTaskRunner extends ServerTaskRunner<TomcatTask>{
	private static final Logger LOG=LoggerFactory.getLogger(TomcatTaskRunner.class);
	
	@Autowired
	private CmdExecutor cmdExecutor;
	

	protected String getExecutable() {
		if (PlatformHelper.isWindows()) {
			return "java.exe";
		} else {
			return "java";
		}
	}
	@Override
	protected ExecutionResult execute(TomcatTask task) {
		InstanceConfig instCfg = getInstanceConfig(task);
		
		String script=null;
		boolean failOnError=false;
		if (task.getCommand().equals("start")) {
			logTask(String.format("Starting Tomcat server instance '%s' using: %s...",instCfg.getNodeName(),instCfg.getServerConfig().getServerStartScript()));
			validateInstance(instCfg);
			
			script = instCfg.getServerConfig().getServerStartScript();
			failOnError=true;
		} else if (task.getCommand().equals("stop")) {
			logTask(String.format("Stopping Tomcat server instance '%s' using: %s...",instCfg.getNodeName(),instCfg.getServerConfig().getServerStopScript()));
			validateInstance(instCfg);
			
			script = instCfg.getServerConfig().getServerStopScript();
			failOnError=false;
			
		
		} else if (task.getCommand().equals("restart")) {
			logTask(String.format("Restarting Tomcat server instance '%s' using: %s...",instCfg.getNodeName(),instCfg.getServerConfig().getServerRestartScript()));
			validateInstance(instCfg);
			script = instCfg.getServerConfig().getServerRestartScript();
			failOnError=true;
		} else {
			throw new IllegalArgumentException("Illegal command: "+task.getCommand());
		}
		
		String[] split = script.split(" ");
		if (split.length>1) {
			StringBuilder argBuilder=new StringBuilder();
			for (int i=1;i<split.length;i++) {
				if (i>1) {
					argBuilder.append(' ');
				}
				argBuilder.append(split[i]);
			}
			task.setArgs(argBuilder.toString());
		}
		task.setPath(split[0]);

		
		
		ExecutionResult ret = super.execute(task);
		
		//waiting after stop
		if (task.getCommand().equals("stop")&&task.getWaitAfterStopSeconds()!=0) {
			LOG.info("Sleeping {} seconds after stop...",task.getWaitAfterStopSeconds());
			try {
				Thread.sleep(task.getWaitAfterStopSeconds()*1000);
			} catch (InterruptedException e) {
				LOG.error("Failed to sleep after stop for {} seconds",e);
			}
		}
		
		//killing after stop
		/*if (task.getCommand().equals("stop")&&task.isKillingIfRunning()) {
			String cmd=getCmdPattern(instCfg);
			LOG.info("Checking tomcat server after stop: java, starting with: {}...",cmd);
			
			boolean res=killIfRunning(getExecutable(),cmd);
			if (res) {
				LOG.info("Successfully killed tomcat server after stop: starting with: {}...",cmd);
			} else {
				LOG.info("Tomcat server not running: starting with: {}...",cmd);
			}
		
		}*/
		return ret;
	}

	private void validateInstance(InstanceConfig instCfg) {
		if (instCfg.getServerConfig().getServerPath()==null) {
			throw new IllegalArgumentException("Server path not specified");
		}
		File serverDir=new File(instCfg.getServerConfig().getServerPath());
		if (!serverDir.exists()||!serverDir.isDirectory()) {
			throw new IllegalArgumentException("Invalid server path: "+instCfg.getServerConfig().getServerPath());
		}
		/*if (instCfg.getServerConfig().getServerStartScript()!=null) {
			File startScript=new File(instCfg.getServerConfig().getServerStartScript().split(" ")[0]);
			if (!startScript.exists()||!startScript.isFile()) {
				throw new IllegalArgumentException("Invalid server start script: "+startScript.getAbsolutePath());
			}	
		}
		if (instCfg.getServerConfig().getServerStopScript()!=null) {
			File stopScript=new File(instCfg.getServerConfig().getServerStopScript().split(" ")[0]);
			if (!stopScript.exists()||!stopScript.isFile()) {
				throw new IllegalArgumentException("Invalid server stop script: "+stopScript.getAbsolutePath());
			}	
		}
		if (instCfg.getServerConfig().getServerRestartScript()!=null) {
			File restartScript=new File(instCfg.getServerConfig().getServerRestartScript().split(" ")[0]);
			if (!restartScript.exists()||!restartScript.isFile()) {
				throw new IllegalArgumentException("Invalid server restart script: "+restartScript.getAbsolutePath());
			}	
		}*/
		
	}

	protected String getCmdPattern(InstanceConfig instCfg) {
		return String.format("-Dcatalina.base=%s",instCfg.getServerConfig().getServerPath());
	}
	
	


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map getEnvironment(TomcatTask task) {
		
		HashMap ret=new HashMap();
		
		if (task.isExportCatalinaHome()) {
			
			String serverPath=getInstanceConfig(task).getServerConfig().getServerPath();
			String val=PlatformHelper.isWindows()?serverPath.replace("/", "\\"):
				serverPath.replace("\\", "/");
			ret.put("CATALINA_HOME", val);
			LOG.debug("Using CATALINA_HOME: {}",val);
		}
		
		if (task.getJavaHome()!=null) {
			ret.put("JAVA_HOME", task.getJavaHome());
			LOG.debug("Using specified JAVA_HOME: {}",task.getJavaHome());
		} else {
			String javaHome = System.getProperty("java.home");
			if (javaHome.contains(" ")) {
				javaHome=String.format("\"%s\"",javaHome);
			}
			ret.put("JRE_HOME", javaHome);
			LOG.debug("Using default JAVA_HOME: {}",javaHome);
		}
		
		return ret;
	}
	@Override
	protected String getTaskName() {
		return "tomcat";
	}

	
	
}
