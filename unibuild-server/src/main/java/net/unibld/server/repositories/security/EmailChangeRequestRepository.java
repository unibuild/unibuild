package net.unibld.server.repositories.security;



import net.unibld.server.entities.security.EmailChangeRequest;

import org.springframework.data.repository.CrudRepository;

public interface EmailChangeRequestRepository extends CrudRepository<EmailChangeRequest, Long> {

	EmailChangeRequest findByTicket(String ticket);
	 
}