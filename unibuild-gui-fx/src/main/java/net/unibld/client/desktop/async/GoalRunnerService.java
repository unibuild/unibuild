package net.unibld.client.desktop.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.unibld.client.desktop.SpringBeanFactory;
import net.unibld.client.desktop.service.ClientGoalRequest;
import net.unibld.client.desktop.service.ClientGoalResult;
import net.unibld.client.desktop.service.ClientGoalRunner;

/**
 * An async JavaFX service that runs a build goal selected by the user
 * @author andor
 *
 */
public class GoalRunnerService extends Service<ClientGoalResult> {
	private static final Logger LOG=LoggerFactory.getLogger(GoalRunnerService.class);
	
	private ClientGoalRequest request;
	
	/**
	 * Constructor with a build goal run request
	 * @param req Goal run request
	 */
	public GoalRunnerService(ClientGoalRequest req) {
		super();
		this.request=req;
	}
	
	@Override
	public Task<ClientGoalResult> createTask() {
		return new GoalRunnerTask();
	}

    private class GoalRunnerTask extends Task<ClientGoalResult> {
    	

        @Override
        protected ClientGoalResult call() throws Exception {
        	LOG.info("Running goal: "+request.getGoal());
			return SpringBeanFactory.getBean(ClientGoalRunner.class).runGoal(request);

        }    
    }
}
