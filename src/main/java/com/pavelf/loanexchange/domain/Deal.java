package com.pavelf.loanexchange.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pavelf.loanexchange.domain.enumeration.DealStatus;
import com.pavelf.loanexchange.domain.enumeration.Period;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

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

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "start_balance", precision = 21, scale = 2, nullable = false)
    private BigDecimal startBalance;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "jhi_percent", precision = 21, scale = 2, nullable = false)
    private BigDecimal percent;

    @DecimalMin(value = "0")
    @Column(name = "fine", precision = 21, scale = 2)
    private BigDecimal fine;

    @NotNull
    @Column(name = "success_rate", nullable = false)
    private Integer successRate;

    @NotNull
    @Min(value = 0)
    @Column(name = "term", nullable = false)
    private Integer term;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_every", nullable = false)
    private Period paymentEvery;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DealStatus status;

    @NotNull
    @Column(name = "auto_payment_enabled", nullable = false)
    private Boolean autoPaymentEnabled;

    @NotNull
    @Column(name = "capitalization", nullable = false)
    private Boolean capitalization;

    @NotNull
    @Column(name = "early_payment", nullable = false)
    private Boolean earlyPayment;

    @ManyToOne
    @JsonIgnoreProperties("deals")
    private User emitter;

    @ManyToOne
    @JsonIgnoreProperties("deals")
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

    public BigDecimal getFine() {
        return fine;
    }

    public Deal fine(BigDecimal fine) {
        this.fine = fine;
        return this;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine;
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

    public Period getPaymentEvery() {
        return paymentEvery;
    }

    public Deal paymentEvery(Period paymentEvery) {
        this.paymentEvery = paymentEvery;
        return this;
    }

    public void setPaymentEvery(Period paymentEvery) {
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

    public Boolean isAutoPaymentEnabled() {
        return autoPaymentEnabled;
    }

    public Deal autoPaymentEnabled(Boolean autoPaymentEnabled) {
        this.autoPaymentEnabled = autoPaymentEnabled;
        return this;
    }

    public void setAutoPaymentEnabled(Boolean autoPaymentEnabled) {
        this.autoPaymentEnabled = autoPaymentEnabled;
    }

    public Boolean isCapitalization() {
        return capitalization;
    }

    public Deal capitalization(Boolean capitalization) {
        this.capitalization = capitalization;
        return this;
    }

    public void setCapitalization(Boolean capitalization) {
        this.capitalization = capitalization;
    }

    public Boolean isEarlyPayment() {
        return earlyPayment;
    }

    public Deal earlyPayment(Boolean earlyPayment) {
        this.earlyPayment = earlyPayment;
        return this;
    }

    public void setEarlyPayment(Boolean earlyPayment) {
        this.earlyPayment = earlyPayment;
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
            ", startBalance=" + getStartBalance() +
            ", percent=" + getPercent() +
            ", fine=" + getFine() +
            ", successRate=" + getSuccessRate() +
            ", term=" + getTerm() +
            ", paymentEvery='" + getPaymentEvery() + "'" +
            ", status='" + getStatus() + "'" +
            ", autoPaymentEnabled='" + isAutoPaymentEnabled() + "'" +
            ", capitalization='" + isCapitalization() + "'" +
            ", earlyPayment='" + isEarlyPayment() + "'" +
            "}";
    }
}
