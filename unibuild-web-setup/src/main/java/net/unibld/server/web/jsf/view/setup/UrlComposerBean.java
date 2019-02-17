package net.unibld.server.web.jsf.view.setup;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import net.unibld.core.persistence.DatabaseType;
import net.unibld.server.service.setup.jdbc.JdbcDefaults;
import net.unibld.server.service.setup.jdbc.JdbcProperties;
import net.unibld.server.service.setup.jdbc.JdbcUrlComposer;
import net.unibld.server.service.setup.jdbc.JdbcUrlComposerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name="urlComposerBean")
@ViewScoped
public class UrlComposerBean implements Serializable {
	private static final Logger LOGGER=LoggerFactory.getLogger(UrlComposerBean.class);
	
	@ManagedProperty(value = "#{setupBean}")
    private SetupBean setupBean;
	
	
	private String host=JdbcDefaults.DEFAULT_HOST;
	private int port=JdbcDefaults.DEFAULT_PORT_MYSQL;
	private String database=JdbcDefaults.DEFAULT_DB;
	private String instance;
	
	private String url;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public void handleKeyEvent() {
		composeUrl();
	}
	private void composeUrl() {
		DatabaseType dbType = setupBean.getModel().getDatabase().getType();
		if (dbType!=null) {
			JdbcUrlComposer composer = JdbcUrlComposerFactory.getComposer(dbType);
			
			JdbcProperties props=new JdbcProperties();
			props.setHost(host);
			props.setPort(port);
			props.setDatabase(database);
			props.setInstance(instance);
			
			this.url=composer.composeJdbcUrl(props);
		}
	}
	public void copy() {
		LOGGER.info("Copying composed URL to DatabaseSetup...");
		setupBean.copyUrl(url);
	}
	public SetupBean getSetupBean() {
		return setupBean;
	}
	public void setSetupBean(SetupBean setupBean) {
		this.setupBean = setupBean;
	}
	public String getUrl() {
		if (url==null && host!=null && port!=0) {
			composeUrl();
		}
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	
	public boolean isInstanceVisible() {
		DatabaseType dbType = setupBean.getModel().getDatabase().getType();
		return dbType==DatabaseType.MSSQL;
	}
	public void updateDefaults(DatabaseType type) {
		this.host=JdbcDefaults.DEFAULT_HOST;
		this.database=JdbcDefaults.DEFAULT_DB;
		switch (type) {
			case MySQL:
				this.port=JdbcDefaults.DEFAULT_PORT_MYSQL;
			case MSSQL:
				this.port=JdbcDefaults.DEFAULT_PORT_MSSQL;
			case Oracle:
				this.port=JdbcDefaults.DEFAULT_PORT_ORACLE;
			case PostgreSQL:
				this.port=JdbcDefaults.DEFAULT_PORT_POSTGRESQL;
		}
		composeUrl();
		
	}
}
