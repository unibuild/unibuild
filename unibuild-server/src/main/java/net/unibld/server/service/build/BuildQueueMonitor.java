package net.unibld.server.service.build;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A Spring component that invokes the checkForRun() method on the {@link BuildQueue} instance to start any builds 
 * that have been previously queued.<br>
 * It is invoked by a Quartz scheduler periodically, by default in every 3 secs (see WEB-INF/server-scheduler.xml)
 * 
 * @author andor
 *
 */
@Component("buildQueueMonitor")
public class BuildQueueMonitor {
	@Autowired
	private BuildQueue buildQueue;
	
	/**
	 * Invokes the checkForRun() method on the {@link BuildQueue} instance to start any builds that have been previously queued
	 */
	public void run() {
		buildQueue.checkForRun();
	}
}
