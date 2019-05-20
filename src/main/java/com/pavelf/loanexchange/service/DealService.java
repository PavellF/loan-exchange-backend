package com.pavelf.loanexchange.service;

import com.pavelf.loanexchange.domain.BalanceLog;
import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.User;
import com.pavelf.loanexchange.domain.enumeration.BalanceLogEvent;
import com.pavelf.loanexchange.domain.enumeration.DealStatus;
import com.pavelf.loanexchange.repository.BalanceLogRepository;
import com.pavelf.loanexchange.repository.DealRepository;
import com.pavelf.loanexchange.security.AuthoritiesConstants;
import com.pavelf.loanexchange.security.SecurityUtils;
import com.pavelf.loanexchange.web.rest.errors.BadRequestAlertException;
import com.pavelf.loanexchange.web.rest.errors.NotEnoughMoneyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.pavelf.loanexchange.security.AuthoritiesConstants.CREDITOR;
import static com.pavelf.loanexchange.security.AuthoritiesConstants.DEBTOR;

/**
 * Service for managing deals.
 */
@Service
public class DealService {

    private final UserService userService;
    private final BalanceLogRepository balanceLogRepository;
    private final DealRepository dealRepository;
    private final BalanceLogService balanceLogService;

    public DealService(UserService userService, BalanceLogRepository balanceLogRepository,
                       DealRepository dealRepository, BalanceLogService balanceLogService) {
        this.userService = userService;
        this.balanceLogRepository = balanceLogRepository;
        this.dealRepository = dealRepository;
        this.balanceLogService = balanceLogService;
    }

    /**
     * Creates new {@link Deal} if possible.
     * @throws NotEnoughMoneyException if not enough money on balance
     * */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Deal createDealForCurrentUser(Deal deal) {
        User loggedInUser = userService.getUserWithAuthorities().get();
        BalanceLog balanceLog = balanceLogRepository.findLastLogForUser().orElseThrow(NotEnoughMoneyException::new);
        BigDecimal currentBalance = balanceLog.getCurrentAccountBalance();

        if (currentBalance.compareTo(deal.getStartBalance()) < 0) {
            throw new NotEnoughMoneyException();
        }

        final Instant now = Instant.now();
        final BigDecimal changed = deal.getStartBalance().negate();

        deal.successRate(computeSuccessRate(deal)).status(DealStatus.PENDING).emitter(loggedInUser)
            .recipient(null).dateOpen(now).dateBecomeActive(null).termDays(computeTermDays(deal, now));

        Deal saved = dealRepository.save(deal);

        BalanceLog minusFromAccount = new BalanceLog().date(now).oldValue(currentBalance)
            .amountChanged(changed).type(BalanceLogEvent.NEW_DEAL_OPEN).account(loggedInUser);

        balanceLogRepository.save(minusFromAccount);

        BalanceLog plusOnDeal = new BalanceLog().date(now).oldValue(BigDecimal.ZERO)
            .amountChanged(deal.getStartBalance()).type(BalanceLogEvent.NEW_DEAL_OPEN).deal(saved);

        balanceLogRepository.save(plusOnDeal);

        BalanceLog instantEarn = balanceLogService.earnPercent(saved.getPercent(), plusOnDeal);
        instantEarn.setDeal(saved);

        balanceLogRepository.save(instantEarn);

        return saved;
    }

    private Integer computeTermDays(Deal deal, Instant now) {
        Instant end;

        if (deal.getPaymentEvery() == ChronoUnit.FOREVER) {
            end = now.plus(deal.getTerm(), ChronoUnit.DAYS);
        } else {
            end = now.plus(deal.getTerm(), deal.getPaymentEvery());
        }

        return (int) Duration.between(now, end).get(ChronoUnit.DAYS);
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

        BigDecimal balanceToSubtract = balanceLogRepository.findAllForDeal(deal).stream()
            .map(BalanceLog::getAmountChanged).reduce(BigDecimal.ZERO, BigDecimal::add);

        BalanceLog plusOnDebtorAccount = new BalanceLog().date(now).amountChanged(balanceToSubtract)
            .type(BalanceLogEvent.LOAN_TAKEN).account(loggedInUser).oldValue(BigDecimal.ZERO);

        balanceLogRepository.findLastLogForUser()
            .ifPresent(log -> plusOnDebtorAccount.oldValue(log.getCurrentAccountBalance()));

        Deal updated = dealRepository.save(deal);
        balanceLogRepository.save(plusOnDebtorAccount);

        return updated;
    }

    /**
     * Finds all deals for creditor or debtor.
     * */
    @Transactional(readOnly = true)
    public Page<Deal> findDealsForLoggedInUser(Pageable pageable) {
        User loggedInUser = userService.getUserWithAuthorities().get();
        Deal deal = new Deal();

        if (loggedInUser.getAuthorities().contains(CREDITOR)) {
            deal.emitter(loggedInUser);
        } else if (loggedInUser.getAuthorities().contains(DEBTOR)) {
            deal.recipient(loggedInUser);
        } else {
            return Page.empty();
        }

        Example<Deal> example = Example.of(deal);
        return dealRepository.findAll(example, pageable);
    }

    /**
     * Finds deal for creditor or debtor by id.
     * */
    @Transactional(readOnly = true)
    public Optional<Deal> findDealByIdForLoggedInUser(Long dealId) {
        User loggedInUser = userService.getUserWithAuthorities().get();
        Deal exampleDeal = new Deal();
        exampleDeal.setId(dealId);

        if (loggedInUser.getAuthorities().contains(CREDITOR)) {
            exampleDeal.emitter(loggedInUser);
        } else if (loggedInUser.getAuthorities().contains(DEBTOR)) {
            exampleDeal.recipient(loggedInUser);
        } else {
            return Optional.empty();
        }

        Example<Deal> example = Example.of(exampleDeal);
        return dealRepository.findOne(example);
    }

    /**
     * Updates deal for creditor.
     * */
    @Transactional
    public Deal updateDeal(Deal toUpdate) {
        Deal deal = dealRepository.findById(toUpdate.getId()).get();

        if (toUpdate.getStatus() == DealStatus.CLOSED) {
            deal.setStatus(DealStatus.CLOSED);
        }

        Deal updated = dealRepository.save(deal);

        return updated;
    }

    private int computeSuccessRate(Deal deal) {
        final int term = deal.getTerm();
        final double fine = deal.getFine().doubleValue();
        final double percent = deal.getPercent().doubleValue();
        final ChronoUnit paymentEvery = deal.getPaymentEvery();
        double successRate = 100;

        // bigger term - bigger successRate
        final int termModifier = 20;
        final int termRate = Math.round(term / termModifier);
        if (termRate < termModifier) {
            successRate = successRate - (termModifier - termRate);
        }

        //bigger rate - lesser successRate
        double rateAtomic = 1;

        if (paymentEvery == ChronoUnit.YEARS) {
            rateAtomic = 1.5;
        } else if (paymentEvery == ChronoUnit.MONTHS) {
            rateAtomic = 2;
        } else if (paymentEvery == ChronoUnit.DAYS) {
            rateAtomic = 18;
        }

        successRate = successRate - rateAtomic * percent;

        if (fine > 0) {
            successRate = successRate - (rateAtomic + 9) * fine;
        }

        if (!deal.isEarlyPayment()) {
            successRate = successRate - 5;
        }

        if (!deal.isCapitalization()) {
            successRate = successRate - 19;
        }

        return Math.max((int) Math.ceil(successRate), 1);
    }
}
