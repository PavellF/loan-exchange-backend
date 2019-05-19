package com.pavelf.loanexchange.web.rest.errors;

public class NotEnoughMoneyException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public NotEnoughMoneyException() {
        super(ErrorConstants.NOT_ENOUGH_MONEY, "Not enough money.", "balanceLog", "nomoney");
    }

}
