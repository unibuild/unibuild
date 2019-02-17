package net.unibld.plugins.cvs;

import java.io.File;
import java.io.IOException;

import javax.net.SocketFactory;

import org.apache.commons.exec.ExecuteException;
import org.netbeans.lib.cvsclient.CVSRoot;
import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.commandLine.BasicListener;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.PServerConnection;
import org.netbeans.modules.versioning.system.cvss.SSHConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.unibld.core.build.BuildException;
import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.lib.DpkgUtils;
import net.unibld.core.security.BuildCredential;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.PlatformHelper;

/**
 * A task runner for CVS tasks, capable of using a local CVS client on Linux by
 * invoking it using commons-exec and a pure Java remote CVS client based on
 * the Netbeans CVS client library and a custom {@link SSHConnection} class.
 * @author andor
 *
 */
@Component
public class CvsTaskRunner extends BaseTaskRunner<CvsTask> {
	private static final Logger LOG=LoggerFactory.getLogger(CvsTaskRunner.class);
	
	private GlobalOptions globalOptions;
	
	@Autowired
	private CmdExecutor cmdExecutor;
	
	@Override
	protected ExecutionResult execute(CvsTask task) {
		
		try {
			if (task.getConnectionType()==CvsTask.CONNECTION_TYPE_LOCAL) {
				return executeLocal(task);
			} else {
				String checkoutPath=new File(task.getCheckoutDir()).getAbsolutePath();
				
				createClient(task,checkoutPath);
				return ExecutionResult.buildSuccess();
			}
		} catch (Exception ex) {
			LOG.error("Failed to execute CVS task",ex);
			return ExecutionResult.buildError("Failed to execute CVS task", ex);
		}
		
	}

	private ExecutionResult executeLocal(CvsTask task) throws ExecuteException, IOException {
		if (PlatformHelper.isWindows()) {
			throw new BuildException("CVS cannot be used locally on Windows");
		}
		
		if (PlatformHelper.isLinuxGeneric()) {
			if (!DpkgUtils.isInstalled(cmdExecutor,"cvs")) {
				return ExecutionResult.buildError("CVS is not installed");
			}
		}
		
		String path=null;
		if (task.getCommand().equals("checkout")) {
			path=task.getCheckoutDir();
		} else if (task.getCommand().equals("export")) {
			path=task.getExportDir();
		}
		
		String branch=task.getBranch();
		String line=String.format("cvs %s%s%s %s",task.getCommand(),path!=null?(" -d "+path):"",
			branch!=null?(" -r "+branch):" -r HEAD",task.getModule());
		if (task.getCommand().equals("checkout")) {
			logTask("Checking out branch {0} from local CVS to: {1}...", branch,path);
		} else if (task.getCommand().equals("export")) {
			logTask("Exporting branch {0} from local CVS to: {1}...", branch,path);
		} else {
			logTask("Executing local CVS command: {0}...", line);
		}
		CmdContext ctx=new CmdContext();
		ctx.setFailOnError(true);
		return cmdExecutor.execCmd(ctx, line);
	}

	private Client createClient(CvsTask task,String basePath) throws AuthenticationException, CommandException, IOException {
		Connection c=createConnection(task);
		Client cl=new Client(c,new StandardAdminHandler());
		cl.setLocalPath(task.getCheckoutDir());
		
        cl.getEventManager().addCVSListener(new BasicListener());

 

//Checkout Command and sets the Modules to checkout with some other options.

        CheckoutCommand command = new CheckoutCommand();
        command.setCheckoutDirectory(task.getCheckoutRelativePath());
        command.setModules(new String[]{task.getRepositoryPath()+"/"+task.getModule()});
        command.setBuilder(null);
        command.setRecursive(true);
        command.setPruneDirectories(true);
        
        command.setResetStickyOnes(true);

 

//Opens and execute the checkout command.

        c.open();

        cl.executeCommand( command, globalOptions );

        c.close(); 

		return cl;
	}

	private Connection createConnection(CvsTask task) throws CommandAbortedException, AuthenticationException {
		if (CvsTask.CONNECTION_TYPE_PSERVER.equals(task.getConnectionType())) {
			PServerConnection c = new PServerConnection();
		    c.setUserName(task.getUserName());
		    c.setEncodedPassword(task.getEncodedPassword());
		    c.setHostName(task.getHostName());
		    c.setRepository(task.getRepositoryPath());        
		    c.open();
		    return c;
		}
		
		if (CvsTask.CONNECTION_TYPE_EXT.equals(task.getConnectionType())) {
			String cvsroot = String.format(":ext:%s@%s:%s",task.getUserName(),task.getHostName(),task.getRepositoryPath());

	        globalOptions = new GlobalOptions();

	        globalOptions.setCVSRoot(CVSRoot.parse(cvsroot).toString());
	        
	      //  ExtConnection c = new ExtConnection(get3rdPartyDirPath()+"\\putty\\PLINK.EXE");
	        SocketFactory sf = SocketFactory.getDefault();
			BuildCredential cred = getPassword(task,cvsroot);
			if (cred==null||cred.getPassword()==null) {
				throw new BuildException("No credentials or password supported");
			}
			SSHConnection sshConnection = new
	        		SSHConnection(sf, task.getHostName(), task.getPort(), task.getUserName(),cred.getPassword());
	        		                 sshConnection.setRepository(task.getRepositoryPath());   
	        //c.open();

	 
		    //return c;
	        		                 
	        return sshConnection;
		}
		throw new IllegalArgumentException("Invalid connection type: "+task.getConnectionType());
	}
	
	private BuildCredential getPassword(CvsTask task,String url) {
		if (url==null||task.getUserName()==null) {
			return null;
		}
		
		return credentialStoreFactory.getStore(task.getPasswordStrategy()).getPassword("cvs", url, task.getUserName());
	}
	

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected String getTaskName() {
		return "cvs";
	}
}
