package com.pavelf.loanexchange.web.rest.specifications;

import com.pavelf.loanexchange.domain.BalanceLog;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BalanceLogSpecification implements Specification<BalanceLog> {

    private Long forUser;
    private Long forDeal;
    private Integer startDaysAgo;
    private Integer endDaysAgo;

    @Override
    public Predicate toPredicate(Root<BalanceLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

        List<Predicate> predicateList = new ArrayList<>();

        if (forUser != null) {
            predicateList.add(cb.equal(root.get("account"), forUser));
        }

        if (forDeal != null) {
            predicateList.add(cb.equal(root.get("deal"), forDeal));
        }

        if (startDaysAgo != null) {
            Instant start = Instant.now().minus(startDaysAgo, ChronoUnit.DAYS);
            predicateList.add(cb.greaterThanOrEqualTo(root.get("date"), start));
        }

        if (endDaysAgo != null) {
            Instant end = Instant.now().minus(endDaysAgo, ChronoUnit.DAYS);
            predicateList.add(cb.lessThanOrEqualTo(root.get("date"), end));
        }

        return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
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

}
