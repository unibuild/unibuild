package net.unibld.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.BuildTask;
import net.unibld.core.Parameter;
import net.unibld.core.ServerDirectory;
import net.unibld.core.build.BuildToolContext;
import net.unibld.core.build.BuildToolContextHolder;
import net.unibld.core.config.BuildGoalConfig;
import net.unibld.core.config.BuildGoalsConfig;
import net.unibld.core.config.InstanceConfig;
import net.unibld.core.config.InstanceConfigurations;
import net.unibld.core.config.LogConfig;
import net.unibld.core.config.LoggerConfig;
import net.unibld.core.config.ProjectConfig;
import net.unibld.core.config.ServerConfig;
import net.unibld.core.config.TaskConfigurations;
import net.unibld.core.config.io.JaxbConfigurator;
import net.unibld.core.config.io.ProjectConfigurator;
import net.unibld.core.log.SimpleConsoleLogger;
import net.unibld.core.task.impl.sys.MkdirTask;
import net.unibld.core.test.UnitTestContext;

/**
 * Tests the XML configuration of projects and builds, using {@link JaxbConfigurator}.
 * @author andor
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class XmlConfiguratorTest {
	@Autowired
	private ProjectConfigurator configurator;
	
	@Autowired
	private BuildToolContextHolder contextHolder;
	/**
	 * Initializes the test
	 */
	@BeforeClass
	public static void init() {
		new File(UnitTestContext.getBaseDir()).mkdirs();
	}
	
	/**
	 * Tests the read and write of static tags defined as JAXB {@link XmlElement}s.
	 * @throws Exception
	 */
	@Test
	public void testWriteRead() throws Exception {
		String baseDir = "./target/"+getClass().getSimpleName();
		new File(baseDir).mkdirs();
		
		ProjectConfig pc = createSampleProject(baseDir);
		
		File outFile = new File(FilenameUtils.concat(baseDir, "out.xml"));
		JaxbConfigurator<ProjectConfig> xmlCnf=new JaxbConfigurator<ProjectConfig>();
		xmlCnf.writeConfig(new FileOutputStream(outFile), pc);
		Assert.assertTrue(outFile.exists());
		Assert.assertTrue(outFile.isFile());
		Assert.assertTrue(outFile.length()>0);
		
		BuildToolContext ctx = contextHolder.createContext(UUID.randomUUID().toString());
		
		ProjectConfig pc2 = xmlCnf.readConfig(ctx,ProjectConfig.class, new FileInputStream(outFile));
		Assert.assertTrue(pc!=null);
		
		assertProjectEquals(pc,pc2);
	}

	/**
	 * Tests the read and write of dynamic tags (such as tasks in project config).
	 * @throws Exception
	 */
	@Test
	public void testWriteReadDynamic() throws Exception {
		String baseDir = "./target/"+getClass().getSimpleName();
		new File(baseDir).mkdirs();
		
		//taskRegistry.initialize();
		
		
		ProjectConfig pc = createSampleProject(baseDir);
		
		File outFile = new File(FilenameUtils.concat(baseDir, "out-dynamic.xml"));
		
		configurator.writeConfig(new FileOutputStream(outFile), pc);
		Assert.assertTrue(outFile.exists());
		Assert.assertTrue(outFile.isFile());
		Assert.assertTrue(outFile.length()>0);
		
		BuildToolContext ctx = contextHolder.createContext(UUID.randomUUID().toString());
		
		ProjectConfig pc2 = configurator.readConfig(ctx,ProjectConfig.class, new FileInputStream(outFile));
		Assert.assertTrue(pc2!=null);
		
		//assertTaskConfigEquals(pc,pc2);
	}
	
	protected ProjectConfig createSampleProject(String baseDir) {
		List<BuildTask> tl=createTasks();
			
		ProjectConfig pc=new ProjectConfig();
		pc.setLogConfig(createLogConfig());
		
		pc.setGoalsConfig(new BuildGoalsConfig());
		pc.getGoalsConfig().setGoals(new ArrayList<BuildGoalConfig>());
		BuildGoalConfig goal = new BuildGoalConfig();
		goal.setName("dump");
		goal.setTasks(new TaskConfigurations());
		goal.getTasks().setTasks(tl);
		pc.getGoalsConfig().getGoals().add(goal);
		
		pc.setCompanyName("UniBuild");
		
		pc.setProductName("UniBuild Test");
		pc.setProjectName("UniBuild");
		pc.setInstances(createInstancesConfig());
		return pc;
	}

	private List<BuildTask> createTasks() {
		List<BuildTask> ret=new ArrayList<BuildTask>();
		MkdirTask svn = new MkdirTask();
		svn.setPath(UnitTestContext.getBaseDir()+"/xmlconfigurationtest");
		
		ret.add(svn);
		
		
		return ret;
	}

	private InstanceConfigurations createInstancesConfig() {
		InstanceConfigurations ret=new InstanceConfigurations();
		ret.setInstanceConfigs(new ArrayList<InstanceConfig>());
		ret.getInstanceConfigs().add(createInstanceConfig("live"));
		ret.getInstanceConfigs().add(createInstanceConfig("test"));
		
		return ret;
	}

	private InstanceConfig createInstanceConfig(String name) {
		InstanceConfig ret=new InstanceConfig();
		ret.setNodeName(name);
		ret.setServerConfig(new ServerConfig());
		ret.getServerConfig().setServerPath("/usr/local/apache/tomcat6");
		ret.getServerConfig().setDirsToClean(Arrays.asList(createServerDir("work")));
		ret.getServerConfig().setServerRestartScript("/etc/init.d/tomcat6 restart");
		ret.getServerConfig().setServerStartScript("/etc/init.d/tomcat6 start");
		ret.getServerConfig().setServerStopScript("/etc/init.d/tomcat6 stop");
		return ret;
		
	}

	private ServerDirectory createServerDir(String path) {
		ServerDirectory ret=new ServerDirectory();
		ret.setPath(path);
		return ret;
	}

	private void assertProjectEquals(ProjectConfig pc, ProjectConfig pc2) {
		Assert.assertNotNull(pc);
		Assert.assertNotNull(pc2);
		
		assertLogConfigEquals(pc.getLogConfig(),pc2.getLogConfig());
		
		
		Assert.assertEquals(pc.getCompanyName(), pc2.getCompanyName());
		Assert.assertEquals(pc.getProductName(), pc2.getProductName());
		Assert.assertEquals(pc.getProjectName(), pc2.getProjectName());
		
		assertGoalsEqual(pc.getGoalsConfig().getGoals(),pc2.getGoalsConfig().getGoals());
	}

	private void assertGoalsEqual(List<BuildGoalConfig> customGoals,
			List<BuildGoalConfig> customGoals2) {
		if (customGoals!=null&&customGoals2!=null) {
			Assert.assertEquals(customGoals.size(), customGoals2.size());
			for (int i=0;i<customGoals.size();i++) {
				BuildGoalConfig cg1=customGoals.get(i);
				BuildGoalConfig cg2=customGoals2.get(i);
				Assert.assertEquals(cg1.getName(), cg2.getName());
				
				Assert.assertEquals(cg1.getTasks().getTasks().size(), cg2.getTasks().getTasks().size());
				for (int j=0;j<cg1.getTasks().getTasks().size();j++) {
					Class<?> cl1=cg1.getTasks().getTasks().get(j).getClass();
					Class<?> cl2=cg2.getTasks().getTasks().get(j).getClass();
					Assert.assertEquals(cl1, cl2);
				}
			}
		}
	}



	private void assertLogConfigEquals(LogConfig lc, LogConfig lc2) {
		Assert.assertNotNull(lc);
		Assert.assertNotNull(lc2);
		
		Assert.assertNotNull(lc.getLoggerConfigs());
		Assert.assertNotNull(lc2.getLoggerConfigs());
		
		Assert.assertEquals(lc.getLoggerConfigs().size(), lc2.getLoggerConfigs().size());
		for (int i=0;i<lc.getLoggerConfigs().size();i++) {
			assertLoggerConfigEquals(lc.getLoggerConfigs().get(i),lc2.getLoggerConfigs().get(i));
		}
		
	}

	private void assertLoggerConfigEquals(LoggerConfig lc,
			LoggerConfig lc2) {
		Assert.assertNotNull(lc);
		Assert.assertNotNull(lc2);
		
		Assert.assertEquals(lc.getLoggerClass(), lc2.getLoggerClass());
		
		Assert.assertNotNull(lc.getParameters());
		Assert.assertNotNull(lc2.getParameters());
		
		for (int i=0;i<lc.getParameters().size();i++) {
			assertParameterEquals(lc.getParameters().get(i),lc2.getParameters().get(i));
		}
		
	}

	private void assertParameterEquals(Parameter p, Parameter p2) {
		Assert.assertNotNull(p);
		Assert.assertNotNull(p2);
		
		Assert.assertEquals(p.getName(), p2.getName());
		Assert.assertEquals(p.getValue(), p2.getValue());
		
	}


	private LogConfig createLogConfig() {
		LogConfig ret=new LogConfig();
		ret.setLoggerConfigs(new ArrayList<LoggerConfig>());
		ret.getLoggerConfigs().add(createLoggerConfig());
		return ret;
	}

	private LoggerConfig createLoggerConfig() {
		LoggerConfig ret=new LoggerConfig();
		ret.setLoggerClass(SimpleConsoleLogger.class.getName());
		ret.setParameters(new ArrayList<Parameter>());
		ret.addParameter("format", "${task} ${level} ${msg}");
		return ret;
	}
}
