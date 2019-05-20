package com.pavelf.loanexchange.web.rest.specifications;

import com.pavelf.loanexchange.domain.BalanceLog;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BalanceLogSpecification implements Specification<BalanceLog> {

    private Long forUser;
    private Long forDeal;
    private Long userParticipatingInDeal;
    private Integer startDaysAgo;
    private Integer endDaysAgo;

    @Override
    public Predicate toPredicate(Root<BalanceLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

        List<Predicate> predicateList = new ArrayList<>();

        if (forUser != null) {
            Join account = root.join("account");
            predicateList.add(cb.equal(account.get("id"), forUser));
        }

        if (forDeal != null) {
            Join deal = root.join("deal");
            predicateList.add(cb.equal(deal.get("id"), forDeal));
        }

        if (userParticipatingInDeal != null) {
            Join emitter = root.join("deal").join("emitter");
            Join recipient = root.join("deal").join("recipient");
            predicateList.add(cb.or(
                cb.equal(emitter.get("id"), userParticipatingInDeal),
                cb.equal(recipient.get("id"), userParticipatingInDeal)
            ));
        }

        if (startDaysAgo != null) {
            Instant start = Instant.now().minus(startDaysAgo, ChronoUnit.DAYS);
            predicateList.add(cb.greaterThanOrEqualTo(root.get("date"), start));
        }

        if (endDaysAgo != null) {
            Instant end = Instant.now().minus(endDaysAgo, ChronoUnit.DAYS);
            predicateList.add(cb.lessThanOrEqualTo(root.get("date"), end));
        }

        return cb.and((Predicate[]) predicateList.toArray());
    }

    public Long getForUser() {
        return forUser;
    }

    public void setForUser(Long forUser) {
        this.forUser = forUser;
    }

    public Long getForDeal() {
        return forDeal;
    }

    public void setForDeal(Long forDeal) {
        this.forDeal = forDeal;
    }

    public Integer getStartDaysAgo() {
        return startDaysAgo;
    }

    public void setStartDaysAgo(Integer startDaysAgo) {
        this.startDaysAgo = startDaysAgo;
    }

    public Integer getEndDaysAgo() {
        return endDaysAgo;
    }

    public void setEndDaysAgo(Integer endDaysAgo) {
        this.endDaysAgo = endDaysAgo;
    }

    public Long getUserParticipatingInDeal() {
        return userParticipatingInDeal;
    }

    public void setUserParticipatingInDeal(Long userParticipatingInDeal) {
        this.userParticipatingInDeal = userParticipatingInDeal;
    }
}
