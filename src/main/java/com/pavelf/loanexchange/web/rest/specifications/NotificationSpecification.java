package com.pavelf.loanexchange.web.rest.specifications;

import com.pavelf.loanexchange.domain.Notification;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationSpecification implements Specification<Notification> {

    private Long forUser;
    private Long forDeal;

    @Override
    public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicateList = new ArrayList<>();

        if (forDeal != null) {
            Join deal = root.join("associatedDeal");
            predicateList.add(cb.equal(deal.get("id"), forDeal));
        }

        if (forUser != null) {
            Join account = root.join("recipient");
            predicateList.add(cb.equal(account.get("id"), forUser));
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
