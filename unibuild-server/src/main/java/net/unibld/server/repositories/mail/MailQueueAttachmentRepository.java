package net.unibld.server.repositories.mail;



import java.util.List;

import net.unibld.server.entities.mail.MailQueueAttachment;
import net.unibld.server.entities.mail.MailQueueMessage;

import org.springframework.data.repository.CrudRepository;

public interface MailQueueAttachmentRepository extends CrudRepository<MailQueueAttachment, Long> {

	List<MailQueueAttachment> findByMessage(MailQueueMessage message);
}