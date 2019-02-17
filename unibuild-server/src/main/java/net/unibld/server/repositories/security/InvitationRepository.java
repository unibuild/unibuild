package net.unibld.server.repositories.security;

import java.util.List;

import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.entities.security.invitation.InvitationStatus;

import org.springframework.data.repository.CrudRepository;

public interface InvitationRepository extends CrudRepository<Invitation, Long> {
	List<Invitation> findByEmail(String email);

	List<Invitation> findByStatus(InvitationStatus status);

	List<Invitation> findAllByOrderByCreateDate();

	long deleteByInvitor(UserProfile profile);

	Invitation findByAuthCodeAndStatus(String authCode, InvitationStatus status);
}