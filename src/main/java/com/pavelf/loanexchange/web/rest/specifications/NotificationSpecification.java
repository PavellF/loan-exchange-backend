package com.pavelf.loanexchange.web.rest.specifications;

import com.pavelf.loanexchange.domain.Notification;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class NotificationSpecification implements Specification<Notification> {

    private Long forUser;
    private Long forDeal;

    @Override
    public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicateList = new ArrayList<>();

        if (forDeal != null) {
            predicateList.add(cb.equal(root.get("associatedDeal"), forDeal));
        }

        if (forUser != null) {
            predicateList.add(cb.equal(root.get("recipient"), forUser));
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
}
