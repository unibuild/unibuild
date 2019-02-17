package net.unibld.core.task.impl.sys;

import net.unibld.core.BuildTask;
import net.unibld.core.security.PasswordStrategy;

/**
 * Base class for SSH-related tasks.
 * @author andor
 *
 */
public abstract class AbstractSshTask extends BuildTask {
	
	private static final long serialVersionUID = -3076978150032215634L;
	private String userName;
	private String host;
	private int port=22;
	private PasswordStrategy passwordStrategy=PasswordStrategy.stored;
	
	private String keyFile;
	private boolean keyPassphrase;
	
	/**
	 * @return SSH username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName SSH username
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return SSH host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host SSH host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return SSH port, defaults to 22
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port SSH port, defaults to 22
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return Password store strategy, defaults to stored.
	 */
	public PasswordStrategy getPasswordStrategy() {
		return passwordStrategy;
	}

	/**
	 * @param passwordStrategy Password store strategy, defaults to stored.
	 */
	public void setPasswordStrategy(PasswordStrategy passwordStrategy) {
		this.passwordStrategy = passwordStrategy;
	}

	/**
	 * @return Key file
	 */
	public String getKeyFile() {
		return keyFile;
	}

	/**
	 * @param keyFile Key file
	 */
	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}

	/**
	 * @return True if the key uses a passphrase
	 */
	public boolean isKeyPassphrase() {
		return keyPassphrase;
	}

	/**
	 * @param keyPassphrase True if the key uses a passphrase
	 */
	public void setKeyPassphrase(boolean keyPassphrase) {
		this.keyPassphrase = keyPassphrase;
	}


	
	
	
	
}
