package net.unibld.core.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverClassNames {
	private static final Logger LOGGER=LoggerFactory.getLogger(DriverClassNames.class);
	
	public static final String DRIVER_CLASS_MYSQL="com.mysql.jdbc.Driver";
	public static final String DRIVER_CLASS_POSTGRESQL="org.postgresql.Driver";
	public static final String DRIVER_CLASS_MSSQL_JTDS="net.sourceforge.jtds.jdbc.Driver";
	public static final String DRIVER_CLASS_ORACLE = "oracle.jdbc.OracleDriver";
	public static final String DRIVER_CLASS_SQLITE="org.sqlite.JDBC";

	private static final String DIALECT_CLASS_MYSQL = "org.hibernate.dialect.MySQLDialect";
	private static final String DIALECT_CLASS_SQLITE = "org.hibernate.dialect.SQLiteDialect";
	private static final String DIALECT_CLASS_MSSQL = "org.hibernate.dialect.SQLServerDialect";
	private static final String DIALECT_CLASS_ORACLE = "org.hibernate.dialect.Oracle10gDialect";
	private static final String DIALECT_CLASS_POSTGRESQL = "org.hibernate.dialect.PostgreSQL82Dialect";
	
	public static final String getDriverClassName(DatabaseType type) {
		switch (type) {
			case MySQL:
				return DRIVER_CLASS_MYSQL;
			case MSSQL:
				return DRIVER_CLASS_MSSQL_JTDS;
			case Oracle:
				return DRIVER_CLASS_ORACLE;
			case PostgreSQL:
				return DRIVER_CLASS_POSTGRESQL;
			case SQLite:
				return DRIVER_CLASS_SQLITE;
		}
		throw new IllegalArgumentException("Invalid database type: "+type);
	}

	public static DatabaseType getDatabaseTypeByDriverClassName(
			String driverClass) {
		if (driverClass==null) {
			throw new IllegalArgumentException("Driver class was null");
		}
		if (driverClass.equals(DRIVER_CLASS_MYSQL)) {
			return DatabaseType.MySQL;
		}
		if (driverClass.equals(DRIVER_CLASS_MSSQL_JTDS)) {
			return DatabaseType.MSSQL;
		}
		if (driverClass.equals(DRIVER_CLASS_POSTGRESQL)) {
			return DatabaseType.PostgreSQL;
		}
		if (driverClass.equals(DRIVER_CLASS_ORACLE)) {
			return DatabaseType.Oracle;
		}
		if (driverClass.equals(DRIVER_CLASS_SQLITE)) {
			return DatabaseType.SQLite;
		}
		//throw new IllegalArgumentException("Invalid driver class: "+driverClass);
		LOGGER.warn("Invalid driver class: "+driverClass);
		return null;
	}
	
	public static final String getDialectClassName(DatabaseType type) {
		switch (type) {
			case MySQL:
				return DIALECT_CLASS_MYSQL;
			case MSSQL:
				return DIALECT_CLASS_MSSQL;
			case Oracle:
				return DIALECT_CLASS_ORACLE;
			case PostgreSQL:
				return DIALECT_CLASS_POSTGRESQL;
			case SQLite:
				return DIALECT_CLASS_SQLITE;
		}
		throw new IllegalArgumentException("Invalid database type: "+type);
	}
}
