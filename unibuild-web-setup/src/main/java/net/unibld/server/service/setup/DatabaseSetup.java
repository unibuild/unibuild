package net.unibld.server.service.setup;

import net.unibld.core.persistence.DatabaseType;

public class DatabaseSetup {
	private DatabaseType type;
	private String url;
	private String user;
	private String password;

	public DatabaseType getType() {
		return type;
	}

	public void setType(DatabaseType type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
