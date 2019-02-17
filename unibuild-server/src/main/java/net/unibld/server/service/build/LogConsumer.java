package net.unibld.server.service.build;

import net.unibld.server.entities.build.BuildLog;

public interface LogConsumer {
	void addLogMessage(String buildId,BuildLog log);

	void appendToLastLog(String buildId,String str);
}
