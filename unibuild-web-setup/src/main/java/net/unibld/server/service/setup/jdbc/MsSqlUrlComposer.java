package net.unibld.server.service.setup.jdbc;

import org.springframework.util.StringUtils;

public class MsSqlUrlComposer implements JdbcUrlComposer {

	@Override
	public String composeJdbcUrl(JdbcProperties properties) {
		
		String url = String.format("jdbc:jtds:sqlserver://%s:%d/%s", 
				properties.getHost(),properties.getPort(),properties.getDatabase());
		if (!StringUtils.isEmpty(properties.getInstance())) {
			url+=(String.format(";instance=%s",properties.getInstance()));
		}
		return url;
	}

}
