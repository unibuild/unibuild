package net.unibld.core.security;

/**
 * An interface that defines a password read method
 * @author andor
 *
 */
public interface IPasswordReaderInterface {

	/**
	 * Reads a password from the implementor
	 * @param credentialType Credential type
	 * @param target Credential realm
	 * @param identity Identity
	 * @return Password read
	 */
	String readPassword(String credentialType, String target, String identity);

}
