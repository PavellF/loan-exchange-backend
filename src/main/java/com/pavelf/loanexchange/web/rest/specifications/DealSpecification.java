package com.pavelf.loanexchange.web.rest.specifications;

import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.enumeration.DealStatus;
import com.pavelf.loanexchange.domain.enumeration.PaymentInterval;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DealSpecification implements Specification<Deal> {

    private Long forEmitter;
    private Long forRecipient;
    private DealStatus withStatus;
    private Long endDateIntervalStart;
    private Long endDateIntervalEnd;
    private Integer withStartBalance;
    private Integer successRate;
    private Long dealId;
    private PaymentInterval paymentEvery;
    private Integer minTerm;
    private Long onlyAvailableToDebtor;

    @Override
    public Predicate toPredicate(Root<Deal> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicateList = new ArrayList<>();

        if (forEmitter != null) {
            Join emitter = root.join("emitter");
            predicateList.add(cb.equal(emitter.get("id"), forEmitter));
        }

        if (forRecipient != null) {
            Join recipient = root.join("recipient");
            predicateList.add(cb.equal(recipient.get("id"), forRecipient));
        }

        if (onlyAvailableToDebtor != null) {
            Join recipient = root.join("recipient");
            predicateList.add(cb.or(
                cb.equal(recipient.get("id"), onlyAvailableToDebtor),
                cb.equal(root.get("status"), DealStatus.PENDING)
            ));
        }

        if (withStatus != null) {
            predicateList.add(cb.equal(root.get("status"), withStatus));
        }

        if (minTerm != null) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get("term"), minTerm));
        }

        if (endDateIntervalEnd != null) {
            Instant endDate = Instant.ofEpochMilli(endDateIntervalEnd);
            predicateList.add(cb.lessThanOrEqualTo(root.get("endDate"), endDate));
        }

        if (endDateIntervalStart != null) {
            Instant endDate = Instant.ofEpochMilli(endDateIntervalStart);
            predicateList.add(cb.greaterThanOrEqualTo(root.get("endDate"), endDate));
        }

        if (withStartBalance != null) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get("startBalance"), BigDecimal.valueOf(withStartBalance)));
        }

        if (successRate != null) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get("successRate"), successRate));
        }

        if (dealId != null) {
            predicateList.add(cb.equal(root.get("id"), dealId));
        }

        if (paymentEvery != null) {
            predicateList.add(cb.equal(root.get("paymentEvery"), paymentEvery));
        }

        return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
    }

    public Long getOnlyAvailableToDebtor() {
        return onlyAvailableToDebtor;
    }

    public void setOnlyAvailableToDebtor(Long onlyAvailableToDebtor) {
        this.onlyAvailableToDebtor = onlyAvailableToDebtor;
    }

    public Integer getMinTerm() {
        return minTerm;
    }

    public void setMinTerm(Integer minTerm) {
        this.minTerm = minTerm;
    }

    public Integer getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Integer successRate) {
        this.successRate = successRate;
    }

    public Long getForEmitter() {
        return forEmitter;
    }

    public void setForEmitter(Long forEmitter) {
        this.forEmitter = forEmitter;
    }

    public Long getForRecipient() {
        return forRecipient;
    }

    public void setForRecipient(Long forRecipient) {
        this.forRecipient = forRecipient;
    }

    public DealStatus getWithStatus() {
        return withStatus;
    }

    public void setWithStatus(DealStatus withStatus) {
        this.withStatus = withStatus;
    }

    public Integer getWithStartBalance() {
        return withStartBalance;
    }

    public void setWithStartBalance(Integer withStartBalance) {
        this.withStartBalance = withStartBalance;
    }

    public Long getDealId() {
        return dealId;
    }

    public void setDealId(Long dealId) {
        this.dealId = dealId;
    }

    public Long getEndDateIntervalStart() {
        return endDateIntervalStart;
    }

    public void setEndDateIntervalStart(Long endDateIntervalStart) {
        this.endDateIntervalStart = endDateIntervalStart;
    }

    public Long getEndDateIntervalEnd() {
        return endDateIntervalEnd;
    }

    public void setEndDateIntervalEnd(Long endDateIntervalEnd) {
        this.endDateIntervalEnd = endDateIntervalEnd;
    }

    public PaymentInterval getPaymentEvery() {
        return paymentEvery;
    }

    public void setPaymentEvery(PaymentInterval paymentEvery) {
        this.paymentEvery = paymentEvery;
    }
}
