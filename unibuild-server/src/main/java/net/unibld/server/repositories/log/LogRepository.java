package net.unibld.server.repositories.log;

import net.unibld.server.entities.log.Log;

import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<Log, Long> {

	long countByEvent(String event);

	

	
}
