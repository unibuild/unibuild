package net.unibld.server.repositories.mail;



import java.util.List;

import net.unibld.server.entities.mail.MailQueueMessage;
import net.unibld.server.entities.mail.MailQueueStatus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface MailQueueRepository extends PagingAndSortingRepository<MailQueueMessage, Long> {
	 @Query(value = "select m from MailQueueMessage as m where m.status = :status order by m.createDate asc")
	 List<MailQueueMessage> findByStatusOrderByCreateDate(@Param("status") MailQueueStatus status,Pageable pageable);
}