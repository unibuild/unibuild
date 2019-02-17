package net.unibld.server.service.log;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.unibld.server.entities.log.Log;
import net.unibld.server.entities.log.LogLevel;
import net.unibld.server.repositories.log.LogRepository;
import net.unibld.server.service.query.AbstractListQueryExecutor;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("logService")
public class LogServiceImpl extends AbstractListQueryExecutor<Log> implements LogService{

	@Autowired
	private LogRepository logRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Log findLog(long id) {
		return logRepo.findOne(id);
	}

	@Override
	public ServiceResult<Log> getList(ListQueryInput input) {
		return new ServiceResult<Log>(getResultList(input,Log.class), getTotalCount(input,Log.class));
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}


	protected String getDefaultOrderColumnName() {
		return "created";
	}

	@Override
	@Transactional
	public Log saveInfoLog(String event,String details,String userId) {
		Log log=new Log();
		log.setLevel(LogLevel.INFO);
		log.setEvent(event);
		log.setDetails(details);
		log.setUserId(userId);
		
		return logRepo.save(log);
	}

	@Override
	public long getLogCountAll() {
		return logRepo.count();
	}

	@Override
	public long getLogCountByEvent(String event) {
		return logRepo.countByEvent(event);
	}
}
