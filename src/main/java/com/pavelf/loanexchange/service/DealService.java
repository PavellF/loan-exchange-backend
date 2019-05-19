package com.pavelf.loanexchange.service;

import com.pavelf.loanexchange.domain.BalanceLog;
import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.User;
import com.pavelf.loanexchange.domain.enumeration.DealStatus;
import com.pavelf.loanexchange.repository.BalanceLogRepository;
import com.pavelf.loanexchange.repository.DealRepository;
import com.pavelf.loanexchange.security.NoUserFoundException;
import com.pavelf.loanexchange.web.rest.errors.NotEnoughMoneyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Service for managing deals.
 */
@Service
public class DealService {

    private final UserService userService;
    private final BalanceLogRepository balanceLogRepository;
    private final DealRepository dealRepository;

    public DealService(UserService userService, BalanceLogRepository balanceLogRepository,
                       DealRepository dealRepository) {
        this.userService = userService;
        this.balanceLogRepository = balanceLogRepository;
        this.dealRepository = dealRepository;
    }

    /**
     * Creates new {@link Deal} if possible.
     * @throws NotEnoughMoneyException if not enough money on balance
     * */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Deal createDealForCurrentUser(Deal deal) {
        User loggedInUser = userService.getUserWithAuthorities().orElseThrow(NoUserFoundException::new);
        BalanceLog balanceLog = balanceLogRepository.findLastLogForUser().orElseThrow(NotEnoughMoneyException::new);
        BigDecimal currentBalance = balanceLog.getCurrentAccountBalance();

        if (currentBalance.compareTo(deal.getStartBalance()) < 0) {
            throw new NotEnoughMoneyException();
        }

        deal.successRate(computeSuccessRate())
            .status(DealStatus.PENDING)
            .emitter(loggedInUser).recipient(null)
            .dateOpen(Instant.now())
            .dateBecomeActive(null);

        Deal saved = dealRepository.save(deal);

        // create deal -> log minus money from accaunt -> log plus for deal

        return saved;
    }

    private int computeSuccessRate() {
        return 0;
    }
}
