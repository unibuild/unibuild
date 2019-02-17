package net.unibld.core.task.impl.sys;

import net.unibld.core.security.BuildCredential;
import net.unibld.core.security.ICredentialStore;
import net.unibld.core.task.BaseTaskRunner;

import org.springframework.util.StringUtils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Abstract base task runner for SSH related tasks 
 * @author andor
 *
 * @param <T> Generic SSH task parameter
 */
public abstract class AbstractSshTaskRunner<T extends AbstractSshTask> extends BaseTaskRunner<T> {
	protected Session createSshSession(T task) throws JSchException {
		JSch jsch = new JSch();
		Session session = jsch.getSession(task.getUserName(), task.getHost(),
				task.getPort());

		String url = String.format("ssh://%s:%d", task.getHost(),
				task.getPort());
		ICredentialStore store = credentialStoreFactory.getStore(task
				.getPasswordStrategy());
		getLogger().info(
				"Checking for SSH auth in credential store {} with strategy: {}...",
				store.getClass().getName(), task.getPasswordStrategy().name());

		
		if (!StringUtils.isEmpty(task.getKeyFile())) {
			if (task.isKeyPassphrase()) {
				BuildCredential cred = store
						.getPassword("ssh-key", url, task.getUserName());
				String passphrase=null;
				if (cred != null) {
					passphrase=cred.getPassword();
				}
				jsch.addIdentity(task.getKeyFile(),passphrase);
			} else {
				jsch.addIdentity(task.getKeyFile());
			}
		} else {
			BuildCredential cred = store
					.getPassword("ssh", url, task.getUserName());
			if (cred != null) {
				session.setPassword(cred.getPassword());
			}
		}
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		return session;
	}
}
