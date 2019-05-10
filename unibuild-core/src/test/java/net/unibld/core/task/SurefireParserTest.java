package net.unibld.core.task;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.unibld.core.task.impl.java.maven.SurefireTestResultParser;
import net.unibld.core.test.TestResults;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/test-context.xml"})
public class SurefireParserTest {
	@Autowired
	private SurefireTestResultParser parser;
	
	@Test
	@Ignore
	public void testSurefireParserDirectory() {
		TestResults results = parser.parseTestResults("c:/tmp/surefire/surefire-reports");
		Assert.assertNotNull(results);
		Assert.assertNotNull(results.getSuites());
		Assert.assertNotEquals(results.getSuites().size(), 0);
	}
}
