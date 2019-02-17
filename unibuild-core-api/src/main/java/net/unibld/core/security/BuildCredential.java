package net.unibld.core.security;

/**
 * A class that represents a credential that is used to access a specific resource during a build
 * run.
 * @author andor
 *
 */
public class BuildCredential {
	private String password;
	private CredentialType type;
	private boolean asked;
	private String userName;
	private String realm;
	private String authProviderType;
	
	/**
	 * @return Password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password Password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return Credential type
	 */
	public CredentialType getType() {
		return type;
	}
	/**
	 * @param type Credential type
	 */
	public void setType(CredentialType type) {
		this.type = type;
	}
	/**
	 * @return True if the user is asked to provide a password during the run.
	 */
	public boolean isAsked() {
		return asked;
	}
	/**
	 * @param asked True if the user is asked to provide a password during the run.
	 */
	public void setAsked(boolean asked) {
		this.asked = asked;
	}
	/**
	 * Creates a credential object with no credentials provided
	 * @return Credential object with no credentials
	 */
	public static BuildCredential createNone() {
		BuildCredential c=new BuildCredential();
		c.setType(CredentialType.NONE);
		return c;
	}
	/**
	 * Creates a credential that is based on a password asked from the user during the build run. 
	 * @param authProviderType Authentication provider
	 * @param target Realm of the credential
	 * @param identity Identity (name)
	 * @param pass Password
	 * @return Credential object
	 */
	public static BuildCredential createPasswordAsked(String authProviderType, String target, String identity, String pass) {
		BuildCredential c=new BuildCredential();
		c.setType(CredentialType.BUILD_PROVIDED);
		c.setPassword(pass);
		c.setAsked(true);
		c.setAuthProviderType(authProviderType);
		c.setRealm(target);
		c.setUserName(identity);
		return c;
	}
	
	/**
	 * Creates a credential based on the store indicating that password management is handled 
	 * by UniBuild
	 * @param authProviderType Authentication provider
	 * @param target Realm of the credential
	 * @param userName User name
	 * @param pass Password
	 * @return Credential object
	 */
	public static BuildCredential createInternalStore(String authProviderType,String target,String userName,String pass) {
		BuildCredential c=new BuildCredential();
		c.setType(CredentialType.BUILD_PROVIDED);
		c.setPassword(pass);
		c.setUserName(userName);
		c.setRealm(target);
		c.setAuthProviderType(authProviderType);
		c.setAsked(false);
		return c;
	}
	/**
	 * Creates a credential based on an external store (such as in the case of SVN) where password
	 * storage is managed by an external software
	 * @param userName User name
	 * @param pass Password
	 * @param realm Realm of the credential
	 * @return Credential object
	 */
	public static BuildCredential createExternalStore(String userName,
			String pass, String realm) {
		BuildCredential c=new BuildCredential();
		c.setType(CredentialType.EXTERNALLY_PROVIDED);
		c.setPassword(pass);
		c.setUserName(userName);
		c.setRealm(realm);
		c.setAsked(false);
		return c;
	}
	/**
	 * @return User name
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName User name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return Realm of the credential (typically a domain or a URL)
	 */
	public String getRealm() {
		return realm;
	}
	/**
	 * @param realm Realm of the credential (typically a domain or a URL)
	 */
	public void setRealm(String realm) {
		this.realm = realm;
	}
	
	/**
	 * @return Authenticator provider (for example: svn)
	 */
	public String getAuthProviderType() {
		return authProviderType;
	}
	/**
	 * @param authProviderType Authenticator provider (for example: svn)
	 */
	public void setAuthProviderType(String authProviderType) {
		this.authProviderType = authProviderType;
	}
}
