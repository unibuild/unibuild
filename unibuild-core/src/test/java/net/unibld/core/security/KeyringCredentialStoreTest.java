package net.unibld.core.security;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import net.unibld.core.config.StoreConfig;
import net.unibld.core.scm.RealmHelper;
import net.unibld.core.test.UnitTestContext;
import net.unibld.core.util.PlatformHelper;

/**
 * Test case for testing {@link KeyringCredentialStore}.
 * @author andor
 *
 */
public class KeyringCredentialStoreTest {
	
	protected static KeyringCredentialStore createStore() {
		String loc=FilenameUtils.concat(UnitTestContext.getBaseDir(),KeyringCredentialStoreTest.class.getSimpleName());
		StoreConfig sc=new StoreConfig();
		sc.setLocation(loc);
		sc.setStrategy(PasswordStrategy.stored);
		
		return new KeyringCredentialStore(sc);
	}
	
	
	@Test
	public void testStoreCredentials() {
		if (!PlatformHelper.isWindows()&&!PlatformHelper.isMac()) {
			LoggerFactory.getLogger(getClass()).info("KeyringCredentialStore is only available on Windows and Mac");
			return;
		}
		KeyringCredentialStore store = createStore();
		
		
		
		String realm=RealmHelper.createRealm("http://ewise.hu/svn/unibuild");
		//BuildCredential pwd0 = store.getPassword("svn", realm, "test-user");
		//Assert.assertNull(pwd0.getPassword());
		
		
		store.setPassword("svn", realm, "test-user", "laci1234");
		
		//store = createStore();
		BuildCredential pwd = store.getPassword("svn", realm, "test-user");
		Assert.assertEquals(pwd.getPassword(), "laci1234");
	}
	
	
}
