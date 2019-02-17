package net.unibld.core.task.impl.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.unibld.core.build.BuildException;
import net.unibld.core.task.ExecutionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Component
public class SshExecTaskRunner extends AbstractSshTaskRunner<SshTask>{
	private static final Logger LOG=LoggerFactory.getLogger(SshExecTaskRunner.class);
	
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "ssh";
	}

	protected ExecutionResult execute(SshTask task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		if (StringUtils.isEmpty(task.getRemoteCommand())) {
			throw new BuildException("Remote command was not specified");
		}
		
		logTask("Executing remote command {0} at {1}:{2} using SSH...", task.getRemoteCommand(),task.getHost(),String.valueOf(task.getPort()));
		
	
		
		Session session=null;
		try {
			session = createSshSession(task);
			executeCommand(session, task.getRemoteCommand(),task.isLogOutput());
			
		} catch (Exception ex) {
			LOG.error("Failed to SSH to: "+task.getHost());
			throw new BuildException("Failed to SSH to: "+task.getHost(),ex);
		} finally {
			if (session!=null) {
				session.disconnect();
			}
		}
		
		return ExecutionResult.buildSuccess();
		
	}

	private void executeCommand(Session session, String remoteCommand,boolean logOutput) throws JSchException, IOException {
		ChannelExec channelExec = (ChannelExec)session.openChannel("exec");

        InputStream in = channelExec.getInputStream();

        channelExec.setCommand(remoteCommand);
        channelExec.connect();

       
        StringBuilder b=new StringBuilder();
        
        byte[] buffer = new byte[1024];
        while (channelExec.getExitStatus() == -1) {
            while (in.available() > 0) {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) {
                    break;
                }
                if (logOutput) {
                	String string = new String(buffer, 0, i);    
                	b.append(string);
                }
             
            }

            if (channelExec.isClosed()) {
                break;
            }

        }

        if (logOutput) {
        	LOG.info("Output: {}", b.toString());
        }
        int exitStatus = channelExec.getExitStatus();
        channelExec.disconnect();
        session.disconnect();
        if(exitStatus < 0){
            LOG.error("SSH remote command completed, but exit status not set.");
            throw new BuildException("Exit status not set: "+exitStatus);
        }
        else if(exitStatus > 0){
            LOG.info("SSH remote command completed, but with errors: {}",exitStatus);
            throw new BuildException("Exited with errors: "+exitStatus);
        }
        else{
            LOG.info("SSH remote command completed successfully.");
        }	
	}
	

}
