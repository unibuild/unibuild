package net.unibld.client.desktop.service;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.unibld.core.BuildProject;
import net.unibld.core.BuildProjectFactory;
import net.unibld.core.VersionInfo;
import net.unibld.core.build.BuildEventListener;
import net.unibld.core.build.BuildRequest;
import net.unibld.core.build.BuildToolContext;
import net.unibld.core.build.BuildToolContextHolder;
import net.unibld.core.build.ProjectBuilder;
import net.unibld.core.config.LoggingConfiguration;
import net.unibld.core.log.Verbosity;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.security.CredentialStoreFactory;
import net.unibld.core.security.ICredentialStore;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.util.PlatformHelper;

@Component
public class ClientGoalRunner {

	private static final Logger LOG = LoggerFactory.getLogger(ClientGoalRunner.class);

	@Autowired
	private BuildToolContextHolder contextHolder;

	@Autowired
	private ProjectBuilder builder;

	@Autowired
	private BuildProjectFactory buildProjectFactory;

	@Autowired
	private CredentialStoreFactory credentialStoreFactory;

	@Autowired
	private LoggingConfiguration loggingConfiguration;

	public ClientGoalResult runGoal(ClientGoalRequest req) {
		final Project proj = req.getProject();
		final String id = UUID.randomUUID().toString();
		final Verbosity verbosity = new Verbosity(false, req.isTraceEnabled());

		loggingConfiguration.init(verbosity.isVerbose(), verbosity.isTrace());
		BuildProject bp = null;

		try {
			req.getUi().clearConsole();
			req.getUi().resetTasksAll();

			BuildToolContext context = contextHolder.createContext(id);
		
			String buildDir = req.getBuildDir();
			new File(buildDir.trim()).mkdirs();
			context.setBuildDir(buildDir.trim());

			ICredentialStore store1 = credentialStoreFactory.getStore(PasswordStrategy.stored);

			// PasswordDialog pwdDlg=new PasswordDialog("OK","Cancel");
			store1.setPasswordReader(req.getUi().getPasswordDialog());
			ICredentialStore store2 = credentialStoreFactory.getStore(PasswordStrategy.ask);
			store2.setPasswordReader(req.getUi().getPasswordDialog());

			File projDir = new File(proj.getPath()).getParentFile();
			context.setProjectDir(projDir.getAbsolutePath());

			context.setProjectFile(proj.getPath());

			bp = buildProjectFactory.loadProject(context, req.getGoal(), proj.getPath(), new String[0]);
			LOG.info("Project loaded: {}", bp.getName());

			String version = VersionInfo.getJarVersion();
			String build = VersionInfo.getJarBuildNumber();
			if (version != null && build != null) {
				System.out.println(
						String.format("UniBuild version: %s, build: %s, goal: %s", version, build, req.getGoal()));
			} else if (version != null) {
				System.out.println(String.format("UniBuild version: %s, build: N/A, goal: %s", version, req.getGoal()));
			} else {
				System.out.println("UniBuild version: N/A, build: N/A, goal: " + req.getGoal());
			}

			System.out.println(context.traceContext(verbosity) + "\n");
			
			if (req.isTraceEnabled()) {
				System.out.println(PlatformHelper.getEnvironmentTrace()+"\n");
			}

			// detecting possible plugins
			//pluginFacade.init(bp, req.getGoal());

			// ui.startConsoleWatch();

			//pluginFacade.onStart(ui);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Initing build failed", ex);
			throw new IllegalStateException("Initing build failed",ex);
		}

		BuildRequest buildRequest=new BuildRequest();
		if (req.getProgressListener()!=null) {
			buildRequest.addProgressListener(req.getProgressListener());
		}
		
		buildRequest.addBuildEventListener(new BuildEventListener() {

			@Override
			public void errorLogCreated(String buildId, String taskClassName, String logFilePath) {

				File f = new File(logFilePath);
				if (f.exists() && f.isFile()) {
					req.getUi().createErrorLogTab(f);
					
				} else {
					LOG.warn("Log file not found: " + logFilePath);
				}
			}

			@Override
			public void buildFailed(String buildId, String taskClassName, int taskIdx, Exception ex) {
				req.getUi().setTaskFailed(taskIdx-1);
			}

			@Override
			public void buildCompleted(String buildId) {
				// TODO Auto-generated method stub

			}

			@Override
			public void buildStarted(Build b) {
				req.getUi().setTaskStarted(0);
			}
		});
		
		ClientGoalResult res=new ClientGoalResult();
		try {
			buildRequest.setBuildId(id);
			buildRequest.setProject(bp);
			buildRequest.setGoal(req.getGoal());
			buildRequest.setVerbosity(verbosity);
			buildRequest.setUserId(PlatformHelper.getLoggedOnUserName());
			builder.run(buildRequest);
			//pluginFacade.onComplete(ui);
			LOG.info("Build goal completed");
			res.setSuccess(true);
			res.setError(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Build goal failed", ex);
			//pluginFacade.onError(ui, ex);
			
			res.setSuccess(false);
			res.setError(ex.getMessage());
		}
		contextHolder.removeContext(id);
		return res;
	}
}
