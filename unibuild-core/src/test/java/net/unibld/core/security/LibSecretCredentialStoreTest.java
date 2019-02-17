package net.unibld.core.security;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import net.unibld.core.config.StoreConfig;
import net.unibld.core.scm.RealmHelper;
import net.unibld.core.test.UnitTestContext;

/**
 * Test case for testing {@link LibSecretCredentialStore}.
 * @author andor
 *
 */
public class LibSecretCredentialStoreTest {
	

	
	protected static LibSecretCredentialStore createStore() {
		String loc=FilenameUtils.concat(UnitTestContext.getBaseDir(),LibSecretCredentialStoreTest.class.getSimpleName());
		StoreConfig sc=new StoreConfig();
		sc.setLocation(loc);
		sc.setStrategy(PasswordStrategy.stored);
		
		return new LibSecretCredentialStore(sc);
	}
	

	
	
	@Test
	@Ignore
	public void testStoreCredentials() {
		
		LibSecretCredentialStore store = createStore();
		
		
		
		String realm=RealmHelper.createRealm("http://ewise.hu/svn/unibuild");
		//BuildCredential pwd0 = store.getPassword("svn", realm, "test-user");
		//Assert.assertNull(pwd0.getPassword());
		
		
		store.setPassword("svn", realm, "testuser", "laci1234");
		
		//store = createStore();
		BuildCredential pwd = store.getPassword("svn", realm, "testuser");
		Assert.assertEquals(pwd.getPassword(), "laci1234");
	}
	
	
}
