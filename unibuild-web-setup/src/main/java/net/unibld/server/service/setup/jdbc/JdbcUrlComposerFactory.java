package net.unibld.server.service.setup.jdbc;

import net.unibld.core.persistence.DatabaseType;

public class JdbcUrlComposerFactory {
	public static JdbcUrlComposer getComposer(DatabaseType type) {
		switch (type) {
			case MySQL:
				return new MySqlUrlComposer();
			case MSSQL:
				return new MsSqlUrlComposer();
			case PostgreSQL:
				return new PostgreSqlUrlComposer();
			case Oracle:
				return new OracleUrlComposer();
		}
		throw new IllegalArgumentException("Unsupported database type: "+type);
	}
}
