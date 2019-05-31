package com.pavelf.loanexchange.service;

import com.pavelf.loanexchange.domain.AccountStats;
import com.pavelf.loanexchange.domain.BalanceLog;
import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.Notification;
import com.pavelf.loanexchange.domain.enumeration.BalanceLogEvent;
import com.pavelf.loanexchange.domain.enumeration.DealStatus;
import com.pavelf.loanexchange.domain.enumeration.PaymentInterval;
import com.pavelf.loanexchange.repository.BalanceLogRepository;
import com.pavelf.loanexchange.repository.DealRepository;
import com.pavelf.loanexchange.repository.NotificationRepository;
import com.pavelf.loanexchange.web.rest.specifications.DealSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BalanceLogService {

    private final BalanceLogRepository balanceLogRepository;
    private final DealRepository dealRepository;
    private final NotificationRepository notificationRepository;
    private final Logger log = LoggerFactory.getLogger(BalanceLogService.class);

    public BalanceLogService(BalanceLogRepository balanceLogRepository, DealRepository dealRepository,
                             NotificationRepository notificationRepository) {
        this.balanceLogRepository = balanceLogRepository;
        this.dealRepository = dealRepository;
        this.notificationRepository = notificationRepository;
    }

    private void processPayments(List<Deal> deals) {
        deals.forEach(deal -> {
            final Instant now = Instant.now();
            final BalanceLog debtorBalance = balanceLogRepository.findLastLogForUser(deal.getRecipient()).get();
            final BalanceLog creditorBalance = balanceLogRepository.findLastLogForUser(deal.getEmitter()).get();
            final BalanceLog dealBalance = balanceLogRepository.findLastLogForDeal(deal).get();
            final BigDecimal averagePayment = deal.getAveragePayment();

            //plus percent on deal balance
            BalanceLog chargePercent = new BalanceLog().date(now).oldValue(dealBalance.getCurrentAccountBalance())
                .amountChanged(deal.getPercentCharge()).type(BalanceLogEvent.PERCENT_CHARGE).deal(deal);
            balanceLogRepository.save(chargePercent);

            //minus on debtor balance
            BalanceLog minusBalance = new BalanceLog().date(now).oldValue(debtorBalance.getCurrentAccountBalance())
                .amountChanged(averagePayment.negate()).type(BalanceLogEvent.DEAL_PAYMENT).account(deal.getRecipient());
            balanceLogRepository.save(minusBalance);

            //minus payment on deal balance
            BalanceLog dealPayment = new BalanceLog().date(now).oldValue(chargePercent.getCurrentAccountBalance())
                .amountChanged(averagePayment.negate()).type(BalanceLogEvent.DEAL_PAYMENT).deal(deal);
            balanceLogRepository.save(dealPayment);

            //plus payment on creditor balance
            BalanceLog addPayment = new BalanceLog().date(now).oldValue(creditorBalance.getCurrentAccountBalance())
                .amountChanged(averagePayment).type(BalanceLogEvent.DEAL_PAYMENT).account(deal.getEmitter());
            balanceLogRepository.save(addPayment);

            //check if deal can be closed
            if (chargePercent.getCurrentAccountBalance().compareTo(BigDecimal.ZERO) == 0) {
                deal.setStatus(DealStatus.SUCCESS);
                dealRepository.save(deal);
                notificationRepository.save(new Notification().date(now).type(BalanceLogEvent.DEAL_CLOSED)
                    .recipient(deal.getRecipient()).associatedDeal(deal));
                notificationRepository.save(new Notification().date(now).type(BalanceLogEvent.DEAL_CLOSED)
                    .recipient(deal.getEmitter()).associatedDeal(deal));
            }
        });
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void chargePercentForEveryDayDeals() {
        log.info("chargePercentForEveryDayDeals() started.");

        DealSpecification specification = new DealSpecification();
        specification.setWithStatus(DealStatus.ACTIVE);
        specification.setPaymentEvery(PaymentInterval.DAY);

        List<Deal> activeDeals = dealRepository.findAll(specification);
        processPayments(activeDeals);

        log.info("chargePercentForEveryDayDeals() ended.");
    }

    @Scheduled(cron = "0 0 0 1 1/1 *", zone = "Europe/Moscow")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void chargePercentForEveryMonthDeals() {
        log.info("chargePercentForEveryMonthDeals() started.");

        DealSpecification specification = new DealSpecification();
        specification.setWithStatus(DealStatus.ACTIVE);
        specification.setPaymentEvery(PaymentInterval.MONTH);

        List<Deal> activeDeals = dealRepository.findAll(specification);
        processPayments(activeDeals);

        log.info("chargePercentForEveryMonthDeals() ended.");
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void chargePercentForSinglePaymentDeals() {
        log.info("chargePercentForSinglePaymentDeals() started.");

        DealSpecification specification = new DealSpecification();
        specification.setWithStatus(DealStatus.ACTIVE);
        specification.setPaymentEvery(PaymentInterval.ONE_TIME);
        specification.setEndDateIntervalEnd(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());

        List<Deal> activeDeals = dealRepository.findAll(specification);
        processPayments(activeDeals);

        log.info("chargePercentForSinglePaymentDeals() ended.");
    }

    public AccountStats getAccountStats(Long forUserId) {
        AccountStats stats = new AccountStats();
        stats.setAllTimeIncoming(balanceLogRepository.getSumWhereChangeIsPositive(forUserId));
        stats.setAllTimePaymentForLoan(balanceLogRepository.getAmountChangedSumForUser(forUserId, BalanceLogEvent.DEAL_PAYMENT));
        return stats;
    }
}
