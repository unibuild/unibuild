package net.unibld.core.security;



/**
 * Credential store interface for credential stores of various types.<br>
 * Implementors should take care of the following:<br>
 * - storing credentials in memory unencrypted or<br>
 * - storing credentials in an encrypted manner using the encryption implementation built in the OS
 * @author andor
 *
 */
public interface ICredentialStore {
	/**
	 * Retrieves password for the specified parameters from the store. If the password
	 * is not stored in the credential store, the user should be asked for it (in 
	 * console mode, typically on stdin).
	 * @param credentialType Credential type
	 * @param target Target URI
	 * @param identity Identity name
	 * @return Credentials stored or asked stored or asked
	 */
	public BuildCredential getPassword(String credentialType,String target,String identity);
	/**
	 * Sets the password for the specified parameters externally (typically from a
	 * unit test)
	 * @param credentialType Credential type
	 * @param target Target URI
	 * @param identity Identity name
	 * @param pwd Password to store
	 */
	public void setPassword(String credentialType,String target,String identity,String pwd);
	/**
	 * Removes all elements from the store.
	 */
	public void clearAll();
	/**
	 * Removes an identity from the store
	 * @param credentialType Credential type
	 * @param target Credential target (where the password applies to)
	 * @param identity Identity of the user
	 */
	public void removeFromStore(String credentialType,String target,String identity);
	/**
	 * Sets the password reader interface to be able to support both command line and GUI based password entry
	 * @param reader Password reader interface 
	 */
	public void setPasswordReader(IPasswordReaderInterface reader);
	
}
