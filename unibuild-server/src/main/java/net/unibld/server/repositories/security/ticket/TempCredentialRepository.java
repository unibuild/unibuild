package net.unibld.server.repositories.security.ticket;



import net.unibld.server.entities.security.ticket.TempCredential;

import org.springframework.data.repository.CrudRepository;

public interface TempCredentialRepository extends CrudRepository<TempCredential, String> {

}