package net.unibld.server.service.security;

import java.util.Date;

import javax.annotation.PostConstruct;

import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.UserStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * Initializes a starting set of users in Spring security based on a text file in the same package.
 * @author andor
 *
 */
@Service
public class UserInitializer {

	private static boolean successfullyRun=false;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserInitializer.class);
	   
	@Autowired
	private SecurityService securityService;
	
	@Value("${init.users.enabled:false}")
	private boolean userInitEnabled;
	@Value("${init.users.administrator.name:}")
	private String adminUserName;
	@Value("${init.users.administrator.email:}")
	private String adminUserEmail;
	@Value("${init.users.administrator.password:}")
	private String adminUserPassword;

	@Autowired
	private TaskExecutor taskExecutor;

	
	@PostConstruct
	public void init() {
		if (!userInitEnabled) {
			LOGGER.info("User init is disabled, skipping...");
			return;
		}
		LOGGER.info("User initializer started.");

	
		doInit();

	}






	public void doInit() {
		if (StringUtils.isEmpty(adminUserName)) {
			throw new IllegalStateException("Admin user name is empty");
		}
		if (StringUtils.isEmpty(adminUserEmail)) {
			throw new IllegalStateException("Admin user email is empty");
		}
		if (StringUtils.isEmpty(adminUserPassword)) {
			throw new IllegalStateException("Admin user password is empty");
		}
		
		if (!securityService.isUserExisting(adminUserName)) {
			UserProfile p=new UserProfile();
			p.setActivated(true);
			p.setUserName(adminUserName);
			p.setEmail(adminUserEmail);
			p.setStatus(UserStatus.REGISTERED);
			p.setFirstName("UniBuild");
			p.setLastName("Administrator");
			
			taskExecutor.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(30000);
						securityService.saveUser(p, Group.ROLE_ADMIN, adminUserPassword);
						LOGGER.info("Registered default admin user: "+adminUserName);
					} catch (SpringSecurityException | InterruptedException e) {
						LOGGER.info("Failed to register default admin user: "+adminUserName+", skipping.",e);
					}
					
				}
			});
			
		} else {
			LOGGER.info("Admin user exists: "+adminUserName+", skipping.");
		}
		
		
		LOGGER.info("User initializer finished.");
		
		successfullyRun=true;
	}

	

	


	/**
	 * @return the successfullyRun
	 */
	public static boolean isSuccessfullyRun() {
		return successfullyRun;
	}






	public String getAdminUserName() {
		return adminUserName;
	}

}
