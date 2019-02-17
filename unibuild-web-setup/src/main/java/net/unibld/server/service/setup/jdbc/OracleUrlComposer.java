package net.unibld.server.service.setup.jdbc;

import org.springframework.util.StringUtils;

public class OracleUrlComposer implements JdbcUrlComposer {

	@Override
	public String composeJdbcUrl(JdbcProperties properties) {
		if (!StringUtils.isEmpty(properties.getDatabase())) {
			return String.format("jdbc:oracle:thin:@%s:%d:%s", 
					properties.getHost(),properties.getPort(),properties.getDatabase());
		} else {
			return String.format("jdbc:oracle:thin:@%s:%d", 
					properties.getHost(),properties.getPort());
		}
	}

}
