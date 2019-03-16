package net.unibld.server.service.setup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import net.unibld.core.persistence.DriverClassNames;
import net.unibld.core.util.GlobalConfigExtractor;
import net.unibld.core.util.PlatformHelper;
import net.unibld.core.util.PropertiesEditor;
import net.unibld.server.spring.ServerConfigExtractor;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("setupService")
public class SetupServiceImpl implements SetupService {
	private static final Logger LOGGER=LoggerFactory.getLogger(SetupServiceImpl.class);
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	@Override
	public void testDatabaseConnection(DatabaseSetup database) throws Exception {
		if (database==null) {
			throw new IllegalArgumentException("DatabaseSetup was null");
		}
		if (StringUtils.isEmpty(database.getUrl())) {
			throw new IllegalArgumentException("Database URL was not specified");
		}
		
        String driverClass = DriverClassNames.getDriverClassName(database.getType());
		Class.forName(driverClass);
        LOGGER.info("Trying to connect to: {} using {}...",database.getUrl(),driverClass);
        Connection connection = DriverManager.getConnection(database.getUrl(),database.getUser(),database.getPassword());

        LOGGER.info("Connection established successfully to database product: {}",
                 connection.getMetaData().getDatabaseProductName());
        
	}

	@Override
	public void testMail(MailSetup mail,String testAddress) {
		if (mail==null) {
			throw new IllegalArgumentException("MailSetup was null");
		}
		if (StringUtils.isEmpty(mail.getHost())) {
			throw new IllegalArgumentException("Mail host was not specified");
		}
		if (StringUtils.isEmpty(testAddress)) {
			throw new IllegalArgumentException("Test address was not specified");
		}
		
		JavaMailSenderImpl sender=new JavaMailSenderImpl();
		sender.setDefaultEncoding("UTF-8");
		sender.setHost(mail.getHost());
		sender.setPort(mail.getPort());
		if (!StringUtils.isEmpty(mail.getUser())) {
			sender.setUsername(mail.getUser());
		}
		if (!StringUtils.isEmpty(mail.getPassword())) {
			sender.setPassword(mail.getPassword());
		}
		
		Properties props=new Properties();
		if (mail.isStartTls()) {
			props.setProperty("mail.smtp.starttls.enable", "true");
		}
		if (mail.isSsl()) {
			props.setProperty("mail.smtp.ssl.enable", "true");
			
		}
		sender.setJavaMailProperties(props);
		
		sender.send(new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage msg) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(msg);
	            message.setTo(testAddress);
	            message.setFrom(mail.getSenderEmail());	
	            
	            message.setSubject("Unibuild server SMTP test");
	            message.setText("This is a test mail from UniBuild server", true);
			}
		});
	}

	@Override
	public SetupModel loadProperties() throws IOException {
		String path = GlobalConfigExtractor.getDefaultConfigPath();
		File file=new File(path);
		if (!file.exists()||!file.isFile()) {
			throw new IllegalStateException("Properties file does not exist: "+path);
		}
		
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(file);
			Properties props=new Properties();
			props.load(fis);
			return SetupModel.valueOf(props);
		} finally {
			if (fis!=null) {
				fis.close();
			}
		}
		
	}

	@Override
	public void saveProperties(SetupModel setup) throws Exception {
		LOGGER.info("Saving global config properties...");
		String cfgPath = GlobalConfigExtractor.getDefaultConfigPath();
		
		if (!isPropertiesExisting()) {
			ServerConfigExtractor.extract(null);
		}
		
		PropertiesEditor editor=new PropertiesEditor(cfgPath, setup.toProperties());
		editor.save(true);
		
		taskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					LOGGER.error("Failed to sleep thread",e);
				}
				LOGGER.info("Deploying server webapp...");
				try {
					if (PlatformHelper.isLinux()) {
						deployServer("sh /etc/unibld/scripts/start-deploy.sh");
					} else if (PlatformHelper.isWindows()) {
						deployServer("cmd.exe /c \"c:\\Program Files\\UniBuild\\deploy-server.bat\"");
					}
				} catch (Exception ex) {
					LOGGER.error("Failed to execute server deploy",ex);
				}
			}
		});
	}

	

	@Override
	public boolean isPropertiesExisting() {
		String path = GlobalConfigExtractor.getDefaultConfigPath();
		File file=new File(path);
		return file.exists() && file.isFile();
	}

	private void deployServer(String cmd) throws ExecuteException, IOException {
		LOGGER.info("Executing deploy: {}...",cmd);
		CommandLine cmdLine = CommandLine.parse(cmd);

		
		DefaultExecutor executor = new DefaultExecutor();
		ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
		
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		executor.setStreamHandler(streamHandler);

		int exitValue = executor.execute(cmdLine);
		
		String output = outputStream.toString();

		
		if (output!=null) {
			LOGGER.info("Output: {}", output);
		}
		

		if (exitValue == 0) {
			LOGGER.info("Successfully executed {}.",cmd);
		} else {
			LOGGER.error(
					"Failed to execute {}, exit value: {}",cmd, 
					exitValue);
			
		}
	}
	

}
