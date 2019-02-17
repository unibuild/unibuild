package net.unibld.core.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;

/**
 * Base class from credential stores of various types, based on their storage device
 * and encryption type.
 * @author andor
 *
 */
public abstract class AbstractCredentialStore implements ICredentialStore {
	protected abstract void putToStore(CredentialKey key, String passStr,String desc);
	protected abstract boolean storeContains(CredentialKey key);
	protected abstract void removeFromStore(CredentialKey key);
	protected abstract String getFromStore(CredentialKey key);
	protected IPasswordReaderInterface passwordReader;
		
	public void removeFromStore(String credentialType,String target,String identity) {
		if (credentialType==null) {
			throw new IllegalArgumentException("Credential type was null");
		}
		if (target==null) {
			throw new IllegalArgumentException("Target was null");
		}
		if (identity==null) {
			throw new IllegalArgumentException("Identity was null");
		}
		CredentialKey key=new CredentialKey(credentialType, target, identity);
		removeFromStore(key);
		LoggerFactory.getLogger(getClass()).info("Removed credential: type={}, target={}, identity={}",credentialType,target,identity);
	}
	/**
	 * Reads the password from the standard user input (stdin)
	 * @param credentialType Credential type
	 * @param target Target URI
	 * @param identity Identity name
	 * @return The password typed
	 */
	protected String readPassword(String credentialType, String target,
			String identity) {
		String passStr=null;
		if (passwordReader!=null) {
			passStr=passwordReader.readPassword(credentialType,target,identity);
		} else if (System.console()!=null) {
			char[] passwd = System.console().readPassword(String.format("Password for %s (%s at %s):",identity,credentialType,target));
			passStr=new String(passwd);
		} else {
			System.out.print(String.format("Password for %s (%s at %s - DEBUG mode):",identity,credentialType,target));

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			try {
				passStr=br.readLine();
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Failed to read password from debug stream",e);
			}
		}
		return passStr;
	}
	public BuildCredential getPassword(String authProviderType,String target, String identity) {
		if (authProviderType==null) {
			throw new IllegalArgumentException("Auth provider type was null");
		}
		if (target==null) {
			throw new IllegalArgumentException("Target was null");
		}
		if (identity==null) {
			throw new IllegalArgumentException("Identity was null");
		}
		CredentialKey key=new CredentialKey(authProviderType, target, identity);
		if (storeContains(key)) {
			return BuildCredential.createInternalStore(authProviderType,target,identity,getFromStore(key));
		}

		LoggerFactory.getLogger(getClass()).info("Credential {}-{}-{} not found in store, asking for it from the user...",
				authProviderType,target,identity);
		String passStr = readPassword(authProviderType, target, identity);
		if (passStr!=null) {
			String desc = getDescription(authProviderType, target);
			LoggerFactory.getLogger(getClass()).info("Storing changes on getPassword() in credential store for: {}...",key.getIdentity());
			putToStore(key, passStr,desc);
			return BuildCredential.createPasswordAsked(authProviderType,target,identity,passStr);
		}
		return BuildCredential.createNone();
	}
	
	private String getDescription(String credentialType, String target) {
		return String.format("%s %s",credentialType,target);
	}
	public void setPassword(String credentialType,String target, String identity,String passwd) {
		if (credentialType==null) {
			throw new IllegalArgumentException("Credential type was null");
		}
		if (target==null) {
			throw new IllegalArgumentException("Target was null");
		}
		if (identity==null) {
			throw new IllegalArgumentException("Identity was null");
		}
		CredentialKey key=new CredentialKey(credentialType, target, identity);
	
		if (storeContains(key)) {
			removeFromStore(key);
		}
		String desc = getDescription(credentialType, target);
		LoggerFactory.getLogger(getClass()).info("Storing changes on setPassword() in credential store for: {}...",key.getIdentity());
		putToStore(key, passwd,desc);
		
		
	}
	/**
	 * Returns the underlying password reader interface or null
	 * @return The underlying password reader interface or null
	 */
	public IPasswordReaderInterface getPasswordReader() {
		return passwordReader;
	}
	public void setPasswordReader(IPasswordReaderInterface passwordReader) {
		this.passwordReader = passwordReader;
	}
	
	
}
