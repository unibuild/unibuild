package net.unibld.server.repositories.security;



import java.util.List;

import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.UserStatus;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserProfileRepository extends CrudRepository<UserProfile, String> {

	List<UserProfile> findByEmail(String email);

	@Query(value = "SELECT OBJECT(p) FROM UserProfile AS p WHERE LOWER(p.userName) LIKE :search OR LOWER(p.email) LIKE :search OR LOWER(p.firstName) LIKE :search OR LOWER(p.lastName) LIKE :search ORDER BY p.userName")
	List<UserProfile> searchUsers(@Param("search") String search);
	@Query(value = "SELECT OBJECT(p) FROM UserProfile AS p WHERE p.activated=false AND p.activationMailSent=false")
	List<UserProfile> findNotActivatedAndNotified();

	List<UserProfile> findByStatus(UserStatus status);
	

}