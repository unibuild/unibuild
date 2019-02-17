package net.unibld.server.repositories.security.ticket;



import java.util.List;

import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.entities.security.ticket.UserTicket.TicketType;

import org.springframework.data.repository.CrudRepository;

public interface UserTicketRepository extends CrudRepository<UserTicket, String> {

	List<UserTicket> findByUserNameAndTypeOrderByCreateDateDesc(
			String userName, TicketType type);

	void deleteByUserNameAndType(String userName, TicketType type);

	List<UserTicket> findByUserNameInAndType(List<String> userIds,
			TicketType remoteAccess);
	
}