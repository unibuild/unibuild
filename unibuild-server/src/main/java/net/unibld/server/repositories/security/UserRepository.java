package net.unibld.server.repositories.security;



import net.unibld.server.entities.security.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

}