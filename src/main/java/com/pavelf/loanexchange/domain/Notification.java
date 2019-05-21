package com.pavelf.loanexchange.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pavelf.loanexchange.domain.enumeration.BalanceLogEvent;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jhi_date", nullable = false)
    private Instant date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "message", nullable = false)
    private BalanceLogEvent message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User recipient;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Deal associatedDeal;

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

    public Notification date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public BalanceLogEvent getMessage() {
        return message;
    }

    public Notification message(BalanceLogEvent message) {
        this.message = message;
        return this;
    }

    public void setMessage(BalanceLogEvent message) {
        this.message = message;
    }

    public User getRecipient() {
        return recipient;
    }

    public Notification recipient(User user) {
        this.recipient = user;
        return this;
    }

    public void setRecipient(User user) {
        this.recipient = user;
    }

    public Deal getAssociatedDeal() {
        return associatedDeal;
    }

    public void setAssociatedDeal(Deal associatedDeal) {
        this.associatedDeal = associatedDeal;
    }

    public Notification associatedDeal(Deal associatedDeal) {
        this.associatedDeal = associatedDeal;
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", message='" + getMessage() + "'" +
            "}";
    }
}
