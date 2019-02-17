package net.unibld.server.service.setup.jdbc;

public interface JdbcUrlComposer {
	String composeJdbcUrl(JdbcProperties properties);
}
