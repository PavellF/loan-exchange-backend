package com.pavelf.loanexchange.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import com.pavelf.loanexchange.domain.enumeration.BalanceLogEvent;

/**
 * A BalanceLog.
 */
@Entity
@Table(name = "balance_log")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BalanceLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jhi_date", nullable = false)
    private Instant date;

    @NotNull
    @Column(name = "old_value", precision = 21, scale = 2, nullable = false)
    private BigDecimal oldValue;

    @NotNull
    @Column(name = "amount_changed", precision = 21, scale = 2, nullable = false)
    private BigDecimal amountChanged;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type", nullable = false)
    private BalanceLogEvent type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Deal deal;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public BalanceLog date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public BigDecimal getOldValue() {
        return oldValue;
    }

    public BalanceLog oldValue(BigDecimal oldValue) {
        this.oldValue = oldValue;
        return this;
    }

    public void setOldValue(BigDecimal oldValue) {
        this.oldValue = oldValue;
    }

    public BigDecimal getAmountChanged() {
        return amountChanged;
    }

    public BalanceLog amountChanged(BigDecimal amountChanged) {
        this.amountChanged = amountChanged;
        return this;
    }

    public void setAmountChanged(BigDecimal amountChanged) {
        this.amountChanged = amountChanged;
    }

    public BalanceLogEvent getType() {
        return type;
    }

    public BalanceLog type(BalanceLogEvent type) {
        this.type = type;
        return this;
    }

    public void setType(BalanceLogEvent type) {
        this.type = type;
    }

    public User getAccount() {
        return account;
    }

    public BalanceLog account(User user) {
        this.account = user;
        return this;
    }

    public void setAccount(User user) {
        this.account = user;
    }

    public Deal getDeal() {
        return deal;
    }

    public BalanceLog deal(Deal deal) {
        this.deal = deal;
        return this;
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
    }

    @JsonIgnore
    public BigDecimal getCurrentAccountBalance() {
        return this.oldValue.add(this.amountChanged);
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BalanceLog)) {
            return false;
        }
        return id != null && id.equals(((BalanceLog) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "BalanceLog{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", oldValue=" + getOldValue() +
            ", amountChanged=" + getAmountChanged() +
            ", type='" + getType() + "'" +
            "}";
    }
}
