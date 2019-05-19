package com.pavelf.loanexchange.repository;

import com.pavelf.loanexchange.domain.BalanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the BalanceLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BalanceLogRepository extends JpaRepository<BalanceLog, Long> {

    @Query("select balanceLog from BalanceLog balanceLog where balanceLog.account.login = ?#{principal.username}")
    List<BalanceLog> findByAccountIsCurrentUser();

}
