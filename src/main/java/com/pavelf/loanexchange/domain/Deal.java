package com.pavelf.loanexchange.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

import com.pavelf.loanexchange.domain.enumeration.PaymentInterval;

import com.pavelf.loanexchange.domain.enumeration.DealStatus;

/**
 * A Deal.
 */
@Entity
@Table(name = "deal")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Deal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "date_open", nullable = false)
    private Instant dateOpen;

    @Column(name = "date_become_active")
    private Instant dateBecomeActive;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "start_balance", precision = 21, scale = 2, nullable = false)
    private BigDecimal startBalance;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "jhi_percent", precision = 21, scale = 2, nullable = false)
    private BigDecimal percent;

    @Column(name = "success_rate", nullable = false)
    private Integer successRate;

    @NotNull
    @Min(value = 0)
    @Column(name = "term", nullable = false)
    private Integer term;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_every", nullable = false)
    private PaymentInterval paymentEvery;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DealStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User emitter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User recipient;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateOpen() {
        return dateOpen;
    }

    public Deal dateOpen(Instant dateOpen) {
        this.dateOpen = dateOpen;
        return this;
    }

    public void setDateOpen(Instant dateOpen) {
        this.dateOpen = dateOpen;
    }

    public Instant getDateBecomeActive() {
        return dateBecomeActive;
    }

    public Deal dateBecomeActive(Instant dateBecomeActive) {
        this.dateBecomeActive = dateBecomeActive;
        return this;
    }

    public void setDateBecomeActive(Instant dateBecomeActive) {
        this.dateBecomeActive = dateBecomeActive;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public Deal endDate(Instant endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getStartBalance() {
        return startBalance;
    }

    public Deal startBalance(BigDecimal startBalance) {
        this.startBalance = startBalance;
        return this;
    }

    public void setStartBalance(BigDecimal startBalance) {
        this.startBalance = startBalance;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public Deal percent(BigDecimal percent) {
        this.percent = percent;
        return this;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public Integer getSuccessRate() {
        return successRate;
    }

    public Deal successRate(Integer successRate) {
        this.successRate = successRate;
        return this;
    }

    public void setSuccessRate(Integer successRate) {
        this.successRate = successRate;
    }

    public Integer getTerm() {
        return term;
    }

    public Deal term(Integer term) {
        this.term = term;
        return this;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public PaymentInterval getPaymentEvery() {
        return paymentEvery;
    }

    public Deal paymentEvery(PaymentInterval paymentEvery) {
        this.paymentEvery = paymentEvery;
        return this;
    }

    public void setPaymentEvery(PaymentInterval paymentEvery) {
        this.paymentEvery = paymentEvery;
    }

    public DealStatus getStatus() {
        return status;
    }

    public Deal status(DealStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(DealStatus status) {
        this.status = status;
    }

    public User getEmitter() {
        return emitter;
    }

    public Deal emitter(User user) {
        this.emitter = user;
        return this;
    }

    public void setEmitter(User user) {
        this.emitter = user;
    }

    public User getRecipient() {
        return recipient;
    }

    public Deal recipient(User user) {
        this.recipient = user;
        return this;
    }

    public void setRecipient(User user) {
        this.recipient = user;
    }

    @JsonIgnore
    public BigDecimal getAveragePayment() {
        if (paymentEvery == PaymentInterval.ONE_TIME) {
            return getStartBalance().multiply(getPercent().add(BigDecimal.ONE));
        }
        BigDecimal term = BigDecimal.valueOf(getTerm());
        BigDecimal overhead = getPercentCharge().multiply(term);
        BigDecimal overallCharged = overhead.add(getStartBalance());
        return overallCharged.divide(term, RoundingMode.HALF_EVEN);
    }

    @JsonIgnore
    public BigDecimal getPercentCharge() {
        return getStartBalance().multiply(getPercent());
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deal)) {
            return false;
        }
        return id != null && id.equals(((Deal) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Deal{" +
            "id=" + getId() +
            ", dateOpen='" + getDateOpen() + "'" +
            ", dateBecomeActive='" + getDateBecomeActive() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", startBalance=" + getStartBalance() +
            ", percent=" + getPercent() +
            ", successRate=" + getSuccessRate() +
            ", term=" + getTerm() +
            ", paymentEvery='" + getPaymentEvery() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
