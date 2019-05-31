package com.pavelf.loanexchange.repository;

import com.pavelf.loanexchange.domain.BalanceLog;
import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.User;
import com.pavelf.loanexchange.domain.enumeration.BalanceLogEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the BalanceLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BalanceLogRepository extends JpaRepository<BalanceLog, Long>, JpaSpecificationExecutor<BalanceLog> {

    @Query("select balanceLog from BalanceLog balanceLog where balanceLog.account.login = ?#{principal.username}")
    List<BalanceLog> findByAccountIsCurrentUser();

    @Query("SELECT b FROM BalanceLog AS b WHERE b.id = " +
        "(SELECT MAX(bl.id) from BalanceLog AS bl WHERE bl.account = ?1)")
    Optional<BalanceLog> findLastLogForUser(User user);

    @Query("SELECT b FROM BalanceLog AS b WHERE b.id = " +
        "(SELECT MAX(bl.id) from BalanceLog AS bl WHERE bl.deal = ?1)")
    Optional<BalanceLog> findLastLogForDeal(Deal deal);

    @Query("SELECT b FROM BalanceLog AS b WHERE b.deal = ?1")
    List<BalanceLog> findAllForDeal(Deal deal);

    @Query("SELECT SUM(l.amountChanged) FROM BalanceLog AS l WHERE l.account.id = ?1 AND l.type = ?2")
    Long getAmountChangedSumForUser(Long userId, BalanceLogEvent forEvent);

    @Query("SELECT SUM(l.amountChanged) FROM BalanceLog AS l WHERE l.amountChanged > 0 AND l.account.id = ?1")
    Long getSumWhereChangeIsPositive(Long userId);
}
