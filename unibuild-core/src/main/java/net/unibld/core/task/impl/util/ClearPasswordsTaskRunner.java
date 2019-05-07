package net.unibld.core.task.impl.util;

import net.unibld.core.security.ICredentialStore;
import net.unibld.core.security.PasswordStrategy;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * A command that cleans passwords stored in the credential stores of the software.
 * It does not clean externally stored passwords (for example SVN) 
 * @author andor
 *
 */
@Component
public final class ClearPasswordsTaskRunner extends BaseTaskRunner<ClearPasswordsTask> {
	private static final Logger LOG=LoggerFactory.getLogger(ClearPasswordsTaskRunner.class);
	
	
	@Override
	public ExecutionResult execute(ClearPasswordsTask task) {

		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
	
		
		try {
			logTask("Deleting stored passwords...");
			
			ICredentialStore stored = credentialStoreFactory.getStore(PasswordStrategy.stored);
			if (stored!=null) {
				stored.clearAll();
			}
			ICredentialStore im = credentialStoreFactory.getStore(PasswordStrategy.ask);
			if (im!=null) {
				im.clearAll();
			}
			return ExecutionResult.buildSuccess();
		} catch (Exception ex) {
			logError("Failed to clear passwords",ex);
			return ExecutionResult.buildError("Failed to clear passwords", ex);
		}
		
	}




	@Override
	protected Logger getLogger() {
		return LOG;
	}


	@Override
	protected String getTaskName() {
		return "clear-passwords";
	}




}
