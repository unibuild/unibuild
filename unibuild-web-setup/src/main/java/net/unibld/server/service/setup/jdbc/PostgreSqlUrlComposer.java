package net.unibld.server.service.setup.jdbc;

public class PostgreSqlUrlComposer implements JdbcUrlComposer {

	@Override
	public String composeJdbcUrl(JdbcProperties properties) {
		return String.format("jdbc:postgresql://%s:%d/%s", 
				properties.getHost(),properties.getPort(),properties.getDatabase());
	}

}
