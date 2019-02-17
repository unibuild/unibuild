package net.unibld.server.service.setup;

import java.util.Properties;

import net.unibld.core.persistence.DatabaseType;
import net.unibld.core.persistence.DriverClassNames;

import org.springframework.util.StringUtils;


public class SetupModel {
	public static final String KEY_PERSISTENCE_DRIVER="persistence.jdbc.driver";
	public static final String KEY_PERSISTENCE_URL="persistence.jdbc.url";
	public static final String KEY_PERSISTENCE_USER="persistence.jdbc.user";
	public static final String KEY_PERSISTENCE_PASSWORD="persistence.jdbc.password";
	public static final String KEY_PERSISTENCE_DIALECT="persistence.hibernate.dialect";
	
	public static final String KEY_MAIL_HOST="mail.smtp.host";
	public static final String KEY_MAIL_PORT="mail.smtp.port";
	public static final String KEY_MAIL_USER="mail.smtp.user";
	public static final String KEY_MAIL_PASSWORD="mail.smtp.password";
	public static final String KEY_MAIL_STARTTLS="mail.smtp.starttls.enable";
	public static final String KEY_MAIL_SSL="mail.smtp.ssl.enable";
	public static final String KEY_MAIL_AUTH="mail.smtp.auth";
	public static final String KEY_MAIL_SENDEREMAIL="mail.smtp.from";
	
	public static final String KEY_ADMIN_USERNAME="init.users.administrator.name";
	public static final String KEY_ADMIN_EMAIL="init.users.administrator.email";
	public static final String KEY_ADMIN_PASSWORD="init.users.administrator.password";
	
	
	
	private DatabaseSetup database=new DatabaseSetup();
	private MailSetup mail=new MailSetup();
	private AdminSetup admin=new AdminSetup();
	
	public DatabaseSetup getDatabase() {
		return database;
	}
	public void setDatabase(DatabaseSetup database) {
		this.database = database;
	}
	public MailSetup getMail() {
		return mail;
	}
	public void setMail(MailSetup mail) {
		this.mail = mail;
	}
	public AdminSetup getAdmin() {
		return admin;
	}
	public void setAdmin(AdminSetup admin) {
		this.admin = admin;
	}
	public static SetupModel valueOf(Properties props) {
		SetupModel model=new SetupModel();
		
		String driverClass=props.getProperty(KEY_PERSISTENCE_DRIVER);
		if (driverClass!=null) {
			DatabaseType type = DriverClassNames.getDatabaseTypeByDriverClassName(driverClass);
			if (type!=null && type!=DatabaseType.Oracle && type!=DatabaseType.SQLite) {
				model.getDatabase().setType(type);
			} else {
				//default to MySQL
				model.getDatabase().setType(DatabaseType.MySQL);
			}
		}
		
		if (model.getDatabase().getType()!=null) {
			model.getDatabase().setUrl(props.getProperty(KEY_PERSISTENCE_URL));
			model.getDatabase().setUser(props.getProperty(KEY_PERSISTENCE_USER));
			model.getDatabase().setPassword(props.getProperty(KEY_PERSISTENCE_PASSWORD));
		}
		
		model.getMail().setHost(props.getProperty(KEY_MAIL_HOST));
		String mport = props.getProperty(KEY_MAIL_PORT);
		if (!StringUtils.isEmpty(mport)) {
			model.getMail().setPort(Integer.parseInt(mport));
		}
		String muser = props.getProperty(KEY_MAIL_USER);
		if (!StringUtils.isEmpty(muser)) {
			model.getMail().setUser(muser);
		}
		String mpwd = props.getProperty(KEY_MAIL_PASSWORD);
		if (!StringUtils.isEmpty(mpwd)) {
			model.getMail().setPassword(mpwd);
		}
		String msender = props.getProperty(KEY_MAIL_SENDEREMAIL);
		if (!StringUtils.isEmpty(msender)) {
			model.getMail().setSenderEmail(msender);
		}
		
		String startTls = props.getProperty(KEY_MAIL_STARTTLS);
		model.getMail().setStartTls("true".equals(startTls));
		
		String ssl = props.getProperty(KEY_MAIL_SSL);
		model.getMail().setSsl("true".equals(ssl));
		
		
		model.getAdmin().setName(props.getProperty(KEY_ADMIN_USERNAME));
		model.getAdmin().setEmail(props.getProperty(KEY_ADMIN_EMAIL));
		model.getAdmin().setPassword(props.getProperty(KEY_ADMIN_PASSWORD));
		return model;
	}
	
	public Properties toProperties() {
		Properties p=new Properties();
		
		//persistence
		p.setProperty(KEY_PERSISTENCE_DRIVER, DriverClassNames.getDriverClassName(database.getType()));
		p.setProperty(KEY_PERSISTENCE_URL, database.getUrl());
		if (!StringUtils.isEmpty(database.getUser())) {
			p.setProperty(KEY_PERSISTENCE_USER, database.getUser());
		} else {
			p.setProperty(KEY_PERSISTENCE_USER, "");
		}
		if (!StringUtils.isEmpty(database.getUser())) {
			p.setProperty(KEY_PERSISTENCE_PASSWORD, database.getPassword());
		} else {
			p.setProperty(KEY_PERSISTENCE_PASSWORD, "");
		}
		p.setProperty(KEY_PERSISTENCE_DIALECT, DriverClassNames.getDialectClassName(database.getType()));
		
		//persistence.unit.name=UniBuildPU
		p.setProperty("persistence.unit.name", "UniBuildPU");
		//persistence.generate.ddl=true
		p.setProperty("persistence.generate.ddl", "true");

		
		//mail
		p.setProperty("mail.send.enabled", "true");
		p.setProperty("mail.return.host", mail.getReturnHost());
		p.setProperty(KEY_MAIL_HOST, mail.getHost());
		p.setProperty(KEY_MAIL_PORT, String.valueOf(mail.getPort()));
		p.setProperty(KEY_MAIL_SENDEREMAIL, mail.getSenderEmail());
		
		boolean hasUser=false;
		boolean hasPwd=false;
		if (!StringUtils.isEmpty(mail.getUser())) {
			p.setProperty(KEY_MAIL_USER, mail.getUser());
			hasUser=true;
		} else {
			p.setProperty(KEY_MAIL_USER, "");
		}
		
		if (!StringUtils.isEmpty(mail.getPassword())) {
			p.setProperty(KEY_MAIL_PASSWORD, mail.getPassword());
			hasPwd=true;
		} else {
			p.setProperty(KEY_MAIL_PASSWORD, "");
		}
		
		if (hasUser&& hasPwd) {
			p.setProperty(KEY_MAIL_AUTH, "true");
		} else {
			p.setProperty(KEY_MAIL_AUTH, "false");
		}
		
		if (mail.isStartTls()) {
			p.setProperty(KEY_MAIL_STARTTLS, "true");
		} else {
			p.setProperty(KEY_MAIL_STARTTLS, "false");
		}
		
		if (mail.isSsl()) {
			p.setProperty(KEY_MAIL_SSL, "true");
		} else {
			p.setProperty(KEY_MAIL_SSL, "false");
		}
		
		//administrator
		p.setProperty("init.users.enabled", "true");
		p.setProperty(KEY_ADMIN_USERNAME, admin.getName());
		p.setProperty(KEY_ADMIN_EMAIL, admin.getEmail());
		p.setProperty(KEY_ADMIN_PASSWORD, admin.getPassword());
		return p;
	}
}
