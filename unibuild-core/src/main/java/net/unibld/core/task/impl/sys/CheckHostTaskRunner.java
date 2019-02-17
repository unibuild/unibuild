package net.unibld.core.task.impl.sys;

import java.net.InetAddress;

import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckHostTaskRunner extends BaseTaskRunner<CheckHostTask> {
	private static final Logger LOG=LoggerFactory.getLogger(CheckHostTaskRunner.class);
	
	@Override
	protected ExecutionResult execute(CheckHostTask task) {
		if (task.getHostName()==null) {
			throw new IllegalArgumentException("Host name not specified");
		}
		logTask("Checking host {0}...", task.getHostName());
		
		
		try {
			InetAddress address = InetAddress.getByName(task.getHostName());
			boolean reachable = address.isReachable(3000);
			if (!reachable) {
				LOG.info("Host '{}' is NOT reachable",task.getHostName());
			} else {
				LOG.info("Host '{}' exists and is reachable",task.getHostName());
			}
			if (!reachable&&task.isExitIfNotAvailable()) {
				return ExecutionResult.buildError(String.format("Host exits but not reachable: '%s'",task.getHostName()));
			} 
			return ExecutionResult.buildSuccess();
		} catch (Exception ex) {
			LOG.info("Host '{}' is NOT found",task.getHostName());
			if (task.isExitIfNotAvailable()) {
				return ExecutionResult.buildError(String.format("Host not available: '%s'",task.getHostName()));
			} else {
				return ExecutionResult.buildSuccess();
			}
		}
		
	}
	

	@Override
	protected String getTaskName() {
		return "check-host";
	}
	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
