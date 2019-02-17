package net.unibld.core.scm;

/**
 * Authentication information stored and managed in the SCM software externally
 * @author andor
 *
 */
public class StoredAuth {
	private String userName;
	private String password;
	private String realm;
	private boolean encrypted;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String domain) {
		this.realm = domain;
	}
	public boolean isEncrypted() {
		return encrypted;
	}
	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}
	public boolean matchRealm(String r) {
		if (r==null||r.trim().length()==0) {
			return false;
		}
		if (realm==null) {
			return false;
		}
		return realm.startsWith(r)||realm.startsWith("<"+r);
	}
	public boolean matchUser(String user) {
		if (userName==null) {
			return false;
		}
		if (user==null) {
			return true;
		}
		return user.equals(userName);
	}
}
