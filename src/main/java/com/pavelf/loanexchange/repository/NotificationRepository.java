package com.pavelf.loanexchange.repository;

import com.pavelf.loanexchange.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select notification from Notification notification where notification.recipient.login = ?#{principal.username}")
    List<Notification> findByRecipientIsCurrentUser();

}
