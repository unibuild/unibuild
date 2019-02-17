package net.unibld.core.task;

import java.io.IOException;

import net.unibld.core.config.TaskConfig;
import net.unibld.core.config.TaskContext;
import net.unibld.core.task.impl.sys.SshExecTaskRunner;
import net.unibld.core.task.impl.sys.SshTask;
import net.unibld.core.test.TestCredentialStoreFactory;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class SshExecTaskRunnerTest {
	private static final String TEST_PWD = "changeme";

	private static final String TEST_COMMAND = "sh ./test.sh";

	private static final String TEST_USER = "testuser";

	private static final String TEST_HOST = "test.host.com";

	@Autowired
	private SshExecTaskRunner runner;

	@Autowired
	private TestCredentialStoreFactory testCredentialStoreFactory;
	
	
	@Test
	@Ignore
	public void testRemoteCommand() throws IOException {
		testCredentialStoreFactory.setUser(TEST_USER);
		testCredentialStoreFactory.setPasswd(TEST_PWD);
		testCredentialStoreFactory.setPasswordOk(true);
		testCredentialStoreFactory.setPasswordAsked(false);
		execute(TEST_HOST,22,TEST_USER,TEST_COMMAND);
		
	}

	private void execute(String host,int port, String user,String command) {
		SshTask t=new SshTask();
		t.setTaskConfig(new TaskConfig());
		t.getTaskConfig().setTaskType("ssh");
		t.getTaskConfig().setTaskContext(new TaskContext());
		
		t.setHost(host);
		t.setPort(port);
		t.setRemoteCommand(command);
		t.setUserName(user);
		runner.run(t);
	}
	
}
