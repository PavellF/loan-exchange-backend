package com.pavelf.loanexchange.web.rest.specifications;

import com.pavelf.loanexchange.domain.Deal;
import com.pavelf.loanexchange.domain.enumeration.DealStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DealSpecification implements Specification<Deal> {

    private Long forEmitter;
    private Long forRecipient;
    private Boolean withEarlyPayment;
    private DealStatus withStatus;
    private Integer forMinAmountOfDays;
    private Integer withStartBalance;
    private Integer successRate;

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

        if (withEarlyPayment != null) {
            predicateList.add(cb.equal(root.get("earlyPayment"), withEarlyPayment));
        }

        if (withStatus != null) {
            predicateList.add(cb.equal(root.get("status"), withStatus));
        }

        if (forMinAmountOfDays != null) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get("termDays"), forMinAmountOfDays));
        }

        if (withStartBalance != null) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get("startBalance"), BigDecimal.valueOf(withStartBalance)));
        }

        if (successRate != null) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get("successRate"), successRate));
        }

        return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
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

    public Boolean getWithEarlyPayment() {
        return withEarlyPayment;
    }

    public void setWithEarlyPayment(Boolean withEarlyPayment) {
        this.withEarlyPayment = withEarlyPayment;
    }

    public DealStatus getWithStatus() {
        return withStatus;
    }

    public void setWithStatus(DealStatus withStatus) {
        this.withStatus = withStatus;
    }

    public Integer getForMinAmountOfDays() {
        return forMinAmountOfDays;
    }

    public void setForMinAmountOfDays(Integer forMinAmountOfDays) {
        this.forMinAmountOfDays = forMinAmountOfDays;
    }

    public Integer getWithStartBalance() {
        return withStartBalance;
    }

    public void setWithStartBalance(Integer withStartBalance) {
        this.withStartBalance = withStartBalance;
    }
}
