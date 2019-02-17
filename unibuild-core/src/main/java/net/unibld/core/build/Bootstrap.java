package net.unibld.core.build;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.unibld.core.BuildProject;
import net.unibld.core.BuildProjectFactory;
import net.unibld.core.VersionInfo;
import net.unibld.core.config.LoggingConfiguration;
import net.unibld.core.log.LogbackLocator;
import net.unibld.core.log.Verbosity;
import net.unibld.core.plugin.PluginDetector;
import net.unibld.core.util.PlatformHelper;

/**
 * Starts and initializes the build tool by creating a BuildToolContext a
 * launching a build session using the context created.
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:15
 */
public class Bootstrap {
	private static final Logger LOG=LoggerFactory.getLogger(Bootstrap.class);
	private static final String SPRING_ROOT_CONTEXT = "/spring/root-context.xml";
	private static final String SPRING_TEST_ROOT_CONTEXT = "/spring/test-context.xml";
	private boolean loggingInitialized;
	
	private BuildToolContext context;
	private ParameterMap parameters;
	private boolean testMode;
	private ClassPathXmlApplicationContext springContext;
	
	
	/**
	 * Instantiates the {@link Bootstrap} class and invokes its start method.
	 * @param args Command line args
	 */
	public static void main(String[] args) {
		Bootstrap boot=new Bootstrap();
		try {
			boot.start(args);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().startsWith(BuildConstants.ERR_PROJECT_FILE_DOES_NOT_EXIST)) {
				try {
					FileUtils.deleteDirectory(new File("./logs"));
				} catch (IOException e1) {
					//do nothing
					e1.printStackTrace();
				}
				System.out.println("Project file does not exist: "+new File("project.xml").getAbsolutePath());
			} else {
				if (boot.loggingInitialized) {
					LOG.error("Failed to run build",e);
				} else {
					System.err.println("Failed to run build");
					e.printStackTrace();
					
				}
			}
			
		 
			
		} catch (Exception e) {
			if (boot.loggingInitialized) {
				LOG.error("Failed to run build",e);
			} else {
				System.err.println("Failed to run build");
				e.printStackTrace();
				
			}
			
		}
	}
	/**
	 * Starts the build tool with the external parameters received
	 * @param args Command line args
	 * @throws BuildException 
	 */
	public void start(String[] args) {
		String buildId=UUID.randomUUID().toString();
		LOG.info("Starting run with id: {}...",buildId);
		
		System.out.println("Initializing UniBuild... ");
		
		
		long initStart=System.currentTimeMillis();
		springContext=new ClassPathXmlApplicationContext(testMode?SPRING_TEST_ROOT_CONTEXT:SPRING_ROOT_CONTEXT);
		parameters=new ParameterMap();
		parameters.initBasicParameters(args);
		
		long initEnd=System.currentTimeMillis();
		System.out.println(String.format("Completed in %d ms.",initEnd-initStart));
		
		Date runStart=new Date(initEnd);
		SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd HH:mm");
		
		if (!parameters.isQuiet()) {
			
			String version = VersionInfo.getJarVersion();
			String build = VersionInfo.getJarBuildNumber();
			String goal = parameters.getGoal();
			if (version!=null&&build!=null) {
				System.out.println(String.format("UniBuild version: %s, build: %s, start: %s, goal: %s", 
					version,build,df.format(runStart),goal));
			} else if (version!=null) {
				System.out.println(String.format("UniBuild version: %s, build: N/A, start: %s, goal: %s",
						version,df.format(runStart),goal));
			} else {
				System.out.println(String.format("UniBuild version: N/A, build: N/A, start: %s, goal: %s",
						df.format(runStart),goal));
			}
			
			System.out.println(parameters.traceParameters());
		
			if (parameters.isTrace()) {
				System.out.println(PlatformHelper.getEnvironmentTrace());
				
			}
		}
		
		if (parameters.isInfo()) {
			LOG.debug("Info-only mode, exiting...");
			return;
		}
		
		initToolLogging();
		LOG.debug("Logger initialized");
		
		springContext.getBean(LoggingConfiguration.class).init(parameters);
		
		context=springContext.getBean(BuildToolContextHolder.class).createContext(buildId);
		processArgs();
		
		try {
			if (parameters.getConfigDir()!=null) {
				LOG.info("Using external config dir: {}",parameters.getConfigDir());
			}
			
		} catch (Exception e) {
			LOG.error("Failed to load global config from classpath",e);
			throw new BuildException("Failed to load global config from classpath",e);
		}
		
		springContext.getBean(PluginDetector.class).detectPlugins();
		
		
		LOG.info("Loading project from: {}...",context.getProjectFile());
		BuildProject p=springContext.getBean(BuildProjectFactory.class).loadProject(context,parameters.getGoal(),context.getProjectFile(),args);
		LOG.info("Project loaded: {}",p.getName());
		
		
		
		ProjectBuilder builder=springContext.getBean(ProjectBuilder.class);
		
		BuildRequest req=new BuildRequest();
		req.setBuildId(buildId);
		req.setProject(p);
		req.setGoal(p.getGoal());
		req.setVerbosity(new Verbosity(parameters.isVerbose(), parameters.isTrace()));
		req.setUserId(PlatformHelper.getLoggedOnUserName());
		
		builder.run(req);
		
		LOG.info("Build of project {} [id={}] completed",p.getName(),p.getId());
		
		springContext.getBean(BuildToolContextHolder.class).removeContext(buildId);
		
		long runEnd=System.currentTimeMillis();
		System.out.println(String.format("Completed in %d ms.",runEnd-initEnd));
		
	}

	
	
	private void initToolLogging() {
		
		try {
			if (!parameters.isVerbose()||parameters.isTrace()) {
				new File("./logs").mkdirs();
			}
			InputStream is = LogbackLocator.locateLogbackConfig(parameters.isTrace(), parameters.isVerbose());
			if (is==null) {
				throw new IllegalStateException("Could not find logback configuration");
			}
			LogbackLocator.configure(is);
			
			LOG.info("SLF4J/Logback configured");
			loggingInitialized=true;
		} catch (Exception e) {
			System.err.println("Failed to init tool logging");
			
			e.printStackTrace();
			throw new BuildException("Failed to init tool logging",e);
		}
		
	}

	private void processArgs() {
		
		Properties props = System.getProperties();
		Enumeration<Object> keys = props.keys();
		StringBuilder b=new StringBuilder();
		
		int i=0;
		while (keys.hasMoreElements()) {
			String key=(String) keys.nextElement();
			String val=props.getProperty(key);
			
			if (i>0) {
				b.append("\n");
			}
			b.append(String.format("%s=%s",key,val));
			i++;
		}
		LOG.debug("System properties:\n{}",b.toString());
		
		
		
		ProjectFileHelper.setupContext(context,parameters.getProjectDir(),parameters.getProjectFile());
		
		
		
		String buildDir=ProjectFileHelper.getDefaultBuildDir(context.getProjectDir());
		context.setBuildDir(buildDir);
		LOG.info("Using build directory: {}...",buildDir);
		
		
	}
	
	
	/**
	 * @return Context for the build tool itself
	 */
	public BuildToolContext getContext() {
		return context;
	}
	/**
	 * @return The parameter map created from the command line arguments
	 */
	public ParameterMap getParameters() {
		return parameters;
	}
	/**
	 * @return True if the tool should be run in test mode
	 */
	public boolean isTestMode() {
		return testMode;
	}
	/**
	 * @param testMode True if the tool should be run in test mode
	 */
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
	
}//end Bootstrap