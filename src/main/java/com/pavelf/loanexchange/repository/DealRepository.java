package com.pavelf.loanexchange.repository;

import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Deal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DealRepository extends JpaRepository<Deal, Long>, JpaSpecificationExecutor<Deal> {

    @Query("select deal from Deal deal where deal.emitter.login = ?#{principal.username}")
    List<Deal> findByEmitterIsCurrentUser();

    @Query("select deal from Deal deal where deal.recipient.login = ?#{principal.username}")
    List<Deal> findByRecipientIsCurrentUser();

    @Query("SELECT COUNT(deal) FROM Deal AS deal WHERE deal.recipient = ?1 AND deal.status = 'ACTIVE'")
    int countActiveDealsForRecipient(User recipient);

    @Query("SELECT COUNT(d) FROM Deal AS d WHERE d.id = ?2 AND (d.recipient = ?1 OR d.emitter = ?1)")
    int isDealExistWithThisUserParticipating(User user, Long dealId);

}
