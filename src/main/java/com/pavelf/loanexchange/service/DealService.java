package com.pavelf.loanexchange.service;

import com.pavelf.loanexchange.domain.BalanceLog;
import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.Notification;
import com.pavelf.loanexchange.domain.User;
import com.pavelf.loanexchange.domain.enumeration.BalanceLogEvent;
import com.pavelf.loanexchange.domain.enumeration.DealStatus;
import com.pavelf.loanexchange.domain.enumeration.PaymentInterval;
import com.pavelf.loanexchange.repository.BalanceLogRepository;
import com.pavelf.loanexchange.repository.DealRepository;
import com.pavelf.loanexchange.repository.NotificationRepository;
import com.pavelf.loanexchange.security.AuthoritiesConstants;
import com.pavelf.loanexchange.security.SecurityUtils;
import com.pavelf.loanexchange.web.rest.errors.BadRequestAlertException;
import com.pavelf.loanexchange.web.rest.errors.NotEnoughMoneyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service for managing deals.
 */
@Service
public class DealService {

    private final UserService userService;
    private final BalanceLogRepository balanceLogRepository;
    private final DealRepository dealRepository;
    private final NotificationRepository notificationRepository;

    public DealService(UserService userService, BalanceLogRepository balanceLogRepository, DealRepository dealRepository,
                       NotificationRepository notificationRepository) {
        this.userService = userService;
        this.balanceLogRepository = balanceLogRepository;
        this.dealRepository = dealRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates new {@link Deal} if possible.
     * @throws NotEnoughMoneyException if not enough money on balance
     * */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Deal createDealForCurrentUser(Deal deal) {
        User loggedInUser = userService.getUserWithAuthorities().get();
        BalanceLog balanceLog = balanceLogRepository.findLastLogForUser(loggedInUser)
            .orElseThrow(NotEnoughMoneyException::new);
        BigDecimal currentBalance = balanceLog.getCurrentAccountBalance();

        if (currentBalance.compareTo(deal.getStartBalance()) < 0) {
            throw new NotEnoughMoneyException();
        }

        final Instant now = Instant.now();

        deal.successRate(computeSuccessRate(deal)).status(DealStatus.PENDING).emitter(loggedInUser)
            .recipient(null).dateOpen(now).dateBecomeActive(null).endDate(null).term(computeTerm(deal));

        Deal saved = dealRepository.save(deal);

        BalanceLog minusFromAccount = new BalanceLog().date(now).oldValue(currentBalance).account(loggedInUser)
            .amountChanged(deal.getStartBalance().negate()).type(BalanceLogEvent.NEW_DEAL_OPEN);

        balanceLogRepository.save(minusFromAccount);

        BalanceLog plusOnDeal = new BalanceLog().date(now).oldValue(BigDecimal.ZERO)
            .amountChanged(deal.getStartBalance()).type(BalanceLogEvent.NEW_DEAL_OPEN).deal(saved);

        balanceLogRepository.save(plusOnDeal);

        return saved;
    }

    private Integer computeTerm(Deal deal) {
        if (deal.getPaymentEvery() == PaymentInterval.MONTH) {
            return deal.getTerm() * 30;
        }
        return deal.getTerm();
    }

    /**
     * Tries to accept deal with current logged in user.
     * @throws BadRequestAlertException if could not process acceptance
     * */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Deal acceptDeal(Deal toProcess) {
        Deal deal = dealRepository.findById(toProcess.getId()).get();

        if (deal.getStatus() != DealStatus.PENDING) {
            throw new BadRequestAlertException("Deal is not in pending status.", "deal", "dealactive");
        }

        User loggedInUser = userService.getUserWithAuthorities().get();
        final Instant now = Instant.now();
        final int activeDealsForRecipient = dealRepository.countActiveDealsForRecipient(loggedInUser);

        if (activeDealsForRecipient > 0 && !SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.SYSTEM)) {
            throw new BadRequestAlertException("Could not possess more than one deals simultaneously.",
                "deal", "doubledeal");
        }

        deal.setStatus(DealStatus.ACTIVE);
        deal.setDateBecomeActive(now);
        deal.setEndDate(now.plus(deal.getTerm(), ChronoUnit.DAYS));

        BigDecimal balance = balanceLogRepository.findLastLogForDeal(deal)
            .map(BalanceLog::getCurrentAccountBalance).orElse(BigDecimal.ZERO);

        BalanceLog plusOnDebtorAccount = new BalanceLog().date(now).amountChanged(balance)
            .type(BalanceLogEvent.LOAN_TAKEN).account(loggedInUser).oldValue(BigDecimal.ZERO);

        balanceLogRepository.findLastLogForUser(loggedInUser)
            .ifPresent(log -> plusOnDebtorAccount.oldValue(log.getCurrentAccountBalance()));

        Deal updated = dealRepository.save(deal);
        balanceLogRepository.save(plusOnDebtorAccount);
        notificationRepository.save(new Notification().date(now).type(BalanceLogEvent.LOAN_TAKEN)
            .recipient(deal.getEmitter()).associatedDeal(deal));

        return updated;
    }

    /**
     * Updates deal for creditor.
     * */
    @Transactional
    public Deal updateDeal(Deal toUpdate) {
        Deal deal = dealRepository.findById(toUpdate.getId()).get();

        if (toUpdate.getStatus() == DealStatus.CLOSED && deal.getStatus() == DealStatus.PENDING) {
            deal.setStatus(DealStatus.CLOSED);
        }

        Deal updated = dealRepository.save(deal);

        return updated;
    }

    private int computeSuccessRate(Deal deal) {
        final int term = deal.getTerm();
        final double percent = deal.getPercent().doubleValue();
        final PaymentInterval paymentEvery = deal.getPaymentEvery();
        double successRate = 100;

        // bigger term - bigger successRate
        final int termModifier = 20;
        final int termRate = Math.round(term / termModifier);
        if (termRate < termModifier) {
            successRate = successRate - (termModifier - termRate);
        }

        //bigger rate - lesser successRate
        double rateAtomic = 1;

        if (paymentEvery == PaymentInterval.MONTH) {
            rateAtomic = 2;
        } else if (paymentEvery == PaymentInterval.DAY) {
            rateAtomic = 18;
        }

        successRate = successRate - rateAtomic * percent;

        return Math.max((int) Math.ceil(successRate), 1);
    }
}
