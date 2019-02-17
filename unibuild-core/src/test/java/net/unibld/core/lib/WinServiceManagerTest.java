package net.unibld.core.lib;

import net.unibld.core.exec.CmdExecutor;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
@Ignore
public class WinServiceManagerTest {
	@Autowired
	private CmdExecutor cmdExec;
	@Test
	public void isTomcatServiceInstalled() throws Exception {
		WinServiceManager wsm=new WinServiceManager();
		boolean serviceInstalled = wsm.isServiceInstalled(cmdExec,"Apache Tomcat");
		
		
	}
}
