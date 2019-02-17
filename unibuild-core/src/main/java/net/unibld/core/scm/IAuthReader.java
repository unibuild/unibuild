package net.unibld.core.scm;

/**
 * An authentication reader interface for SCM support that reads credentials from a store.
 * @author andor
 *
 */
public interface IAuthReader {
	/**
	 * Reads credentials of a user at the specified URL
	 * @param url URL
	 * @param user User name
	 * @return Credentials
	 */
	public StoredAuth readStoredAuth(String url,String user);
}
