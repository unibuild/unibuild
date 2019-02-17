package net.unibld.core.security;

import java.util.Hashtable;
import java.util.Map;

/**
 * An in-memory credential store that stores password in the memory unencrypted.<br>
 * Passwords are lost between runs or restarts in the case of the server.
 * @author andor
 *
 */
public class InMemoryCredentialStore extends AbstractCredentialStore implements ICredentialStore {
	private Map<CredentialKey,String> passwords=new Hashtable<>();
	
	protected void putToStore(CredentialKey key, String passStr,String desc) {
		passwords.put(key,passStr);
	}
	protected boolean storeContains(CredentialKey key) {
		return passwords.containsKey(key);
	}
	protected String getFromStore(CredentialKey key) {
		return passwords.get(key);
	}
	@Override
	protected void removeFromStore(CredentialKey key) {
		passwords.remove(key);
	}
	@Override
	public void clearAll() {
		passwords.clear();
	}
	

}
