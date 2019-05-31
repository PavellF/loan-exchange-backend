package com.pavelf.loanexchange.domain;

public class AccountStats {

    private Long allTimeIncoming;
    private Long allTimePaymentForLoan;

    public Long getAllTimeIncoming() {
        return allTimeIncoming;
    }

    public void setAllTimeIncoming(Long allTimeIncoming) {
        this.allTimeIncoming = allTimeIncoming;
    }

    public Long getAllTimePaymentForLoan() {
        return allTimePaymentForLoan;
    }

    public void setAllTimePaymentForLoan(Long allTimePaymentForLoan) {
        this.allTimePaymentForLoan = allTimePaymentForLoan;
    }
}
