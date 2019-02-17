package net.unibld.core.security;


/**
 * A credential store factory interface.
 * @author andor
 *
 */
public interface ICredentialStoreFactory {

	/**
	 * Returns a credential store depending on the password strategy specified
	 * @param strategy Password strategy
	 * @return Credential store implementation
	 */
	public abstract ICredentialStore getStore(PasswordStrategy strategy);

}