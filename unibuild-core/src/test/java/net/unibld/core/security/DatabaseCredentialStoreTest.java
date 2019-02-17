package net.unibld.core.security;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.config.StoreConfig;
import net.unibld.core.scm.RealmHelper;
import net.unibld.core.service.SecretStoreService;
import net.unibld.core.test.UnitTestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class DatabaseCredentialStoreTest {
	@Autowired
	private SecretStoreService secretStoreService;
	
	protected DatabaseSecretCredentialStore createStore() {
		String loc=FilenameUtils.concat(UnitTestContext.getBaseDir(),LibSecretCredentialStoreTest.class.getSimpleName());
		StoreConfig sc=new StoreConfig();
		sc.setLocation(loc);
		sc.setStrategy(PasswordStrategy.stored);
		
		DatabaseSecretCredentialStore ret = new DatabaseSecretCredentialStore();
		ret.setSecretStoreService(secretStoreService);
		return ret;
	}
	

	
	
	@Test
	public void testStoreCredentials() {
		
		DatabaseSecretCredentialStore store = createStore();
		
		
		
		String realm=RealmHelper.createRealm("http://ewise.hu/svn/unibuild");
		
		
		store.setPassword("svn", realm, "testuser", "laci1234");
		
		BuildCredential pwd = store.getPassword("svn", realm, "testuser");
		Assert.assertEquals(pwd.getPassword(), "laci1234");
	}
}
