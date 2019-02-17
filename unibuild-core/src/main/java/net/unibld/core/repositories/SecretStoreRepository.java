package net.unibld.core.repositories;

import org.springframework.data.repository.CrudRepository;

import net.unibld.core.persistence.model.SecretStore;

/**
 * A Spring Data repository interface for the entity {@link SecretStore}.
 * @author andor
 *
 */
public interface SecretStoreRepository extends CrudRepository<SecretStore, String>{
	
}
