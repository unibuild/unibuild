package net.unibld.core.test;

import java.util.HashSet;
import java.util.Set;

import net.unibld.core.security.BuildCredential;
import net.unibld.core.security.CredentialKey;
import net.unibld.core.security.ICredentialStore;
import net.unibld.core.security.ICredentialStoreFactory;
import net.unibld.core.security.IPasswordReaderInterface;
import net.unibld.core.security.PasswordStrategy;

/**
 * A credential store factory to mock a real credential store factory during unit tests
 * while leaving no footprints on the file system.<br>
 * It creates instances of {@link TestCredentialStore}.
 * @author andor
 *
 */
public class TestCredentialStoreFactory implements ICredentialStoreFactory {
	private boolean passwordOk;
	private TestCredentialStore store;
	private String user;
	private String passwd;
	private boolean passwordAsked;

	public static class TestCredentialStore implements ICredentialStore {
		private boolean rightPassword;
		private boolean asked;
		private Set<CredentialKey> removed=new HashSet<CredentialKey>();
		private String userName;
		private String password;
		private IPasswordReaderInterface passwordReader;
		public TestCredentialStore(String userName,String password) {
			this.rightPassword=true;
			this.userName=userName;
			this.password=password;
		}
		public TestCredentialStore(String userName,String pass,boolean ok,boolean asked) {
			this.rightPassword=ok;
			this.password=pass;
			this.userName=userName;
			this.asked=asked;
		}
		@Override
		public BuildCredential getPassword(String credentialType, String target,
				String identity) {
			if (userName.equals(identity)) {
				if (asked) {
					if (rightPassword) {
						return BuildCredential.createPasswordAsked(credentialType,target,identity,password);
					} else {
						return BuildCredential.createPasswordAsked(credentialType,target,identity,"error");
					}
				} else {
					if (rightPassword) {
						return BuildCredential.createInternalStore(credentialType,target,identity,password);
					} else {
						return BuildCredential.createInternalStore(credentialType,target,identity,"error");
					}
				}
			}
			return null;
		}

		@Override
		public void setPassword(String credentialType, String target,
				String identity, String pwd) {
			this.userName=identity;
			this.password=pwd;
		}

		@Override
		public void clearAll() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeFromStore(String type, String target,
				String identity) {
			CredentialKey key=new CredentialKey(type, target, identity);
			removed.add(key);
		}
		@Override
		public void setPasswordReader(IPasswordReaderInterface reader) {
			this.passwordReader=reader;
			
		}
		
	}
	@Override
	public ICredentialStore getStore(PasswordStrategy strategy) {
		if (store==null) {
			store=new TestCredentialStore(user,passwd,passwordOk,passwordAsked);
		}
		return store;
	}

	public boolean isRemoved(String type, String target,
				String identity) {
		if (store==null) {
			return false;
		}
		CredentialKey key=new CredentialKey(type, target, identity);
		return store.removed.contains(key);
	}

	public boolean isPasswordOk() {
		return passwordOk;
	}

	public void setPasswordOk(boolean passwordOk) {
		this.passwordOk = passwordOk;
	}

	public TestCredentialStore getStore() {
		return store;
	}

	public void setStore(TestCredentialStore store) {
		this.store = store;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public boolean isPasswordAsked() {
		return passwordAsked;
	}

	public void setPasswordAsked(boolean passwordAsked) {
		this.passwordAsked = passwordAsked;
	}
}
