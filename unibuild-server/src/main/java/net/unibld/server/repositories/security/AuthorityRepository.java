package net.unibld.server.repositories.security;



import java.util.List;

import net.unibld.server.entities.security.Authority;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {
	 @Query(value = "SELECT OBJECT(a) FROM Authority as a WHERE a.user.username=:username")
	 List<Authority> findByUserName(@Param("username") String username);
}