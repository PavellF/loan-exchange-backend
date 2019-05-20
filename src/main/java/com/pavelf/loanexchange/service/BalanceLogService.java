package com.pavelf.loanexchange.service;

import com.pavelf.loanexchange.domain.BalanceLog;
import com.pavelf.loanexchange.domain.enumeration.BalanceLogEvent;
import com.pavelf.loanexchange.repository.BalanceLogRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class BalanceLogService {

    private final UserService userService;
    private final BalanceLogRepository balanceLogRepository;

    public BalanceLogService(UserService userService, BalanceLogRepository balanceLogRepository) {
        this.userService = userService;
        this.balanceLogRepository = balanceLogRepository;
    }

    /**
     * Creates log that multiply deal balance based on last balance log.
     * @param percent multiply to
     * @param last last balance log used as a base
     * */
    public BalanceLog earnPercent(BigDecimal percent, BalanceLog last) {
        final Instant now = Instant.now();
        final BigDecimal accountBalance = last.getCurrentAccountBalance();
        final BigDecimal newBalance = accountBalance.multiply(percent.add(BigDecimal.ONE));
        final BigDecimal amountChanged = newBalance.subtract(accountBalance);
        return new BalanceLog().date(now).amountChanged(amountChanged).type(BalanceLogEvent.PERCENT_EARN)
            .oldValue(accountBalance);
    }

}
