	package net.unibld.plugins.svn;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.unibld.core.scm.RealmHelper;
import net.unibld.core.scm.StoredAuth;
import net.unibld.core.test.UnitTestContext;

import org.junit.Assert;
import org.junit.Test;

public class SvnAuthReaderTest {
	@Test 
	public void testReadAuthRealmMatch() throws URISyntaxException, IOException {
		SvnAuthReader r=new SvnAuthReader();
		
		StoredAuth a=r.parseFile(new File(UnitTestContext.extractResource(getClass(),"/test.auth")));
		Assert.assertNotNull(a);
		Assert.assertNotNull(a.getUserName());
		Assert.assertEquals(a.getUserName(),"test-user");
		Assert.assertTrue(a.matchRealm(RealmHelper.createRealm("http://ewise.hu/svn/svn-test")));
	}
	
	


}
