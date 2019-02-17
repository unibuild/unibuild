package net.unibld.server.service.log;

import net.unibld.server.entities.log.Log;
import net.unibld.server.service.query.IListQueryExecutor;

public interface LogService extends IListQueryExecutor<Log> {

	Log findLog(long id);
	Log saveInfoLog(String event,String details,String userId);
	
	long getLogCountAll();
	long getLogCountByEvent(String event);
}
