package net.unibld.server.service.build;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import net.unibld.core.BuildProject;
import net.unibld.core.BuildProjectFactory;
import net.unibld.core.VersionInfo;
import net.unibld.core.build.BuildRequest;
import net.unibld.core.build.BuildToolContext;
import net.unibld.core.build.BuildToolContextHolder;
import net.unibld.core.build.ProjectBuilder;
import net.unibld.core.build.ProjectFileHelper;
import net.unibld.core.config.LoggingConfiguration;
import net.unibld.core.log.LoggingScheme;
import net.unibld.core.log.Verbosity;
import net.unibld.core.persistence.model.Project;
import net.unibld.core.security.ICredentialStore;
import net.unibld.core.security.ICredentialStoreFactory;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.server.entities.build.BuildLog;
import net.unibld.server.entities.log.LogLevel;
import net.unibld.server.service.security.ProjectSecurityService;

/**
 * Spring queue implementation for queueing build runs.
 * @author andor
 *
 */
@Service("buildQueue")
public class BuildQueueImpl implements BuildQueue{
	private static final Logger LOG=LoggerFactory.getLogger(BuildQueueImpl.class);
	
	@Autowired
	private BuildToolContextHolder contextHolder;
	
	@Autowired
	private ProjectBuilder builder;
	
	@Autowired
	private LoggingConfiguration loggingConfiguration;
	@Autowired
	private LoggingScheme loggingScheme;
	
	@Autowired
	private TaskExecutor executor;
	
	@Autowired
	private BuildProjectFactory projectFactory;
	
	@Autowired
	private ICredentialStoreFactory credentialStoreFactory;
	
	@Autowired
	private ProjectSecurityService projectSecurityService;
	
	private List<BuildQueueItem> queue=new Vector<>();
	private BuildQueueItem currentItem=null;

	private BuildQueueItem lastItem;

	
	
	@Override
	public boolean addToQueue(BuildQueueRequest req) {
		if (req==null) {
			throw new IllegalArgumentException("BuildQueueRequest was null");
		}
		if (req.getGoal()==null) {
			throw new IllegalArgumentException("Goal was null");
		}
		if (req.getProject()==null) {
			throw new IllegalArgumentException("Project was null");
		}
		if (req.getUserId()==null) {
			throw new IllegalArgumentException("User ID was null");
		}
		if (!projectSecurityService.checkProjectUserAccess(req.getProject(), req.getGoal(), req.getUserId())) {
			throw new SecurityException("User has no right to goal: "+req.getGoal());
		}
		checkQueueForSame(req.getProject(),req.getGoal());
		
		BuildQueueItem item=new BuildQueueItem(req.getProject(), req.getGoal(),req.getListener());
		item.setUserId(req.getUserId());
		item.setTrace(req.isTrace());
		item.setExternalLogger(req.getExternalLogger());
		item.setLogConsumer(req.getLogConsumer());
		item.setPasswordReader(req.getPasswordReader());
		queue.add(item);
		
		if (currentItem==null&&!queue.isEmpty()) {
			currentItem=queue.get(0);
			startItem(currentItem);
			return currentItem.equals(item);
		} else {
			return false;
		}
	}
	
	private void checkQueueForSame(Project project, String goal) {
		for (BuildQueueItem item:queue) {
			if (item.getProject().getId().equals(project.getId())&&item.getGoal().equals(goal)) {
				throw new IllegalStateException(String.format("The build queue already contains %s %s",project.getName(),goal));
			}
		}
		
	}

	public void startItem(BuildQueueItem item) {
		LOG.info("Starting goal {} of project: {}...",item.getGoal(),item.getProject().getName());
		
		final String id=UUID.randomUUID().toString();
		final Verbosity verbosity=new Verbosity(item.isVerbose(), item.isTrace());
		item.setBuildId(id);
		
		loggingConfiguration.init(verbosity.isVerbose(), verbosity.isTrace());
		if (item.getExternalLogger()!=null) {
			item.getExternalLogger().setBuildId(id);
			loggingScheme.addLogger(item.getExternalLogger());
		}
		BuildProject bpl=null;
		
		
		try {
			BuildToolContext context = contextHolder.createContext(id);
			
			File projDir=new File(item.getProject().getPath()).getParentFile();
			
			context.setBuildDir(ProjectFileHelper.getDefaultBuildDir(projDir.getAbsolutePath()));
			new File(context.getBuildDir()).mkdirs();
			
			
			ICredentialStore store1 = credentialStoreFactory.getStore(PasswordStrategy.stored);
			store1.setPasswordReader(item.getPasswordReader());
			ICredentialStore store2 = credentialStoreFactory.getStore(PasswordStrategy.ask);
			store2.setPasswordReader(item.getPasswordReader());
			
			context.setProjectDir(projDir.getAbsolutePath());
			
			context.setProjectFile(item.getProject().getPath());
			
			bpl=projectFactory.loadProject(context,item.getGoal(),item.getProject().getPath(),new String[0]);
			LOG.info("Project loaded: {}",bpl.getName());
			
			String version = VersionInfo.getJarVersion();
			String build = VersionInfo.getJarBuildNumber();
			if (version!=null&&build!=null) {
				System.out.println(String.format("UniBuild version: %s, build: %s, goal: %s",
					version,build,item.getGoal()));
			} else if (version!=null) {
				System.out.println(String.format("UniBuild version: %s, build: N/A, goal: %s",
						version,item.getGoal()));
			} else {
				System.out.println("UniBuild version: N/A, build: N/A, goal: "+item.getGoal());
			}
			
			System.out.println(context.traceContext(verbosity)+"\n");
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Initing build failed",ex);
			stopped();
			return;
		}
		
		final BuildProject bp=bpl;
		
		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				BuildRequest req=new BuildRequest();
				if (item.getListener()!=null) {
					req.addBuildEventListener(item.getListener());
					req.addProgressListener(item.getListener());
				}
				try {
					req.setBuildId(id);
					req.setProject(bp);
					req.setGoal(item.getGoal());
					req.setVerbosity(verbosity);
					req.setUserId(item.getUserId());
					builder.run(req);
					if (item.getLogConsumer()!=null) {
						BuildLog l=new BuildLog();
						l.setDate(new Date());
						l.setLevel(LogLevel.INFO);
						l.setMessage("GOAL COMPLETED SUCCESSFULLY.");
						item.getLogConsumer().addLogMessage(id,l);
					}
					if (item.getListener()!=null) {
						item.getListener().progress(id,100,bp.getTasks().size());
					}
					LOG.info("Build goal completed");
				} catch (Exception ex) {
					ex.printStackTrace();
					LOG.error("Build goal failed",ex);
					
				}
				contextHolder.removeContext(id);
				stopped();
				
			}
		});
		
		
		
	
	}

	private void stopped() {
		if (currentItem!=null) {
			queue.remove(currentItem);
		}
		lastItem = currentItem;
		currentItem=null;
	}

	@Override
	public boolean isRunning() {
		return currentItem!=null;
	}
	
	@Override
	public boolean isRunningOrCompleted() {
		return currentItem!=null || lastItem!=null;
	}

	@Override
	public String getCurrentOrLastBuildLabel() {
		if (currentItem!=null) {
			return currentItem.getLabel();
		}
		if (lastItem!=null) {
			return lastItem.getLabel();
		}
		return null;
	}

	@Override
	public boolean isBuildRunning(String id) {
		return contextHolder.containsContextId(id);
	}

	@Override
	public String getCurrentBuildId() {
		if (currentItem==null) {
			return null;
		}
		return currentItem.getBuildId();
	}
	
	@Override
	public String getLastBuildId() {
		if (lastItem==null) {
			return null;
		}
		return lastItem.getBuildId();
	}

	@Override
	public void checkForRun() {
		if (currentItem==null && !queue.isEmpty()) {
			currentItem=queue.get(0);
			LOG.info("Starting build automatically: {}:{}...",currentItem.getProject().getName(),
					currentItem.getGoal());
			startItem(currentItem);
		} 
	}

	@Override
	public int getWaitingQueueSize() {
		if (currentItem==null) {
			return queue.size();
		}
		return queue.size()-1;
	}

	@Override
	public String getWaitingQueueContent() {
		StringBuilder b=new StringBuilder();
		int idx=0;
		for (BuildQueueItem i:queue) {
			if (currentItem==null || i.getBuildId()==null||!i.getBuildId().equals(currentItem.getBuildId())) {
				if (idx>0) {
					b.append(", ");
				}
				b.append(i.getProject().getName());
				b.append(' ');
				b.append(i.getGoal());
				idx++;
			}
		}
		return b.toString();
	}

	@Override
	public void cancelCurrent() {
		if (currentItem!=null) {
			builder.cancelBuild(currentItem.getBuildId());
		}
	}

	@Override
	public void cancelWaiting() {
		List<BuildQueueItem> toRemove=new ArrayList<>();
		for (BuildQueueItem i:queue) {
			if (currentItem==null || i.getBuildId()==null||!i.getBuildId().equals(currentItem.getBuildId())) {
				toRemove.add(i);
			}
		}
		
		for (BuildQueueItem r:toRemove) {
			queue.remove(r);
		}
	}

}
