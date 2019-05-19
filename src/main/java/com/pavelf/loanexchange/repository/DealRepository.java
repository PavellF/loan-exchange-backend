package com.pavelf.loanexchange.repository;

import com.pavelf.loanexchange.domain.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Deal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {

    @Query("select deal from Deal deal where deal.emitter.login = ?#{principal.username}")
    List<Deal> findByEmitterIsCurrentUser();

    @Query("select deal from Deal deal where deal.recipient.login = ?#{principal.username}")
    List<Deal> findByRecipientIsCurrentUser();

}
