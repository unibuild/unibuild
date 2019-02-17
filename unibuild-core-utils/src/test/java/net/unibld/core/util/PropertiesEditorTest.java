package net.unibld.core.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import net.unibld.core.test.UnitTestContext;

import org.junit.Test;

public class PropertiesEditorTest {
	@Test
	public void testPropertiesEdit() throws URISyntaxException, IOException {
		String path = UnitTestContext.extractResource(PropertiesEditorTest.class, "/test.global.linux.properties");
		
		Properties p=new Properties();
		//persistence.jdbc.driver=com.mysql.jdbc.Driver
		p.setProperty("persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		//persistence.jdbc.url=jdbc:mysql://localhost:3306/unibuild?useUnicode=true&characterEncoding=UTF-8
		p.setProperty("persistence.jdbc.url", "jdbc:mysql://localhost:3306/unibuild?useUnicode=true&characterEncoding=UTF-8");
		//persistence.jdbc.user=testuser
		p.setProperty("persistence.jdbc.user", "testuser");
		//persistence.jdbc.password=changeme
		p.setProperty("persistence.jdbc.password", "changeme");
		//persistence.hibernate.dialect=org.hibernate.dialect.MySQLDialect
		p.setProperty("persistence.hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		
		//mail.smtp.host=smtp.gmail.com
		p.setProperty("mail.smtp.host", "smtp.gmail.com");
		//mail.smtp.port=587
		p.setProperty("mail.smtp.port", "587");
		//mail.smtp.from=developer@unibld.net
		p.setProperty("mail.smtp.from", "developer@unibld.net");
		//mail.smtp.user=developer@unibld.net
		p.setProperty("mail.smtp.user", "developer@unibld.net");
		//mail.smtp.password=changeme
		p.setProperty("mail.smtp.password", "changeme");
		//mail.smtp.starttls.enable=true
		p.setProperty("mail.smtp.starttls.enable", "true");
		
		PropertiesEditor editor=new PropertiesEditor(path, p);
		editor.save(true);
		
	}
}
