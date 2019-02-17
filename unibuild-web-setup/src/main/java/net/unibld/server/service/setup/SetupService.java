package net.unibld.server.service.setup;

import java.io.IOException;

/**
 * Service interface for a web-based setup
 * @author andor
 *
 */
public interface SetupService {
	/**
	 * Tests a database connection during setup
	 * @param database Database connection to test
	 * @throws Exception If an error occurs
	 */
	void testDatabaseConnection(DatabaseSetup database) throws Exception;

	/**
	 * Tests a mail account during setup
	 * @param mail Mail account to test
	 * @param testAddress Test address to send mail to
	 */
	void testMail(MailSetup mail, String testAddress);

	SetupModel loadProperties() throws IOException;
	
	void saveProperties(SetupModel setup) throws Exception;

	boolean isPropertiesExisting();
}
