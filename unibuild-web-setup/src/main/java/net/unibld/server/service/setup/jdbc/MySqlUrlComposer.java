package net.unibld.server.service.setup.jdbc;

public class MySqlUrlComposer implements JdbcUrlComposer {

	@Override
	public String composeJdbcUrl(JdbcProperties properties) {
		return String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8", 
				properties.getHost(),properties.getPort(),properties.getDatabase());
	}

}
