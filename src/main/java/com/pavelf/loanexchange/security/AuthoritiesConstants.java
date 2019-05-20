package com.pavelf.loanexchange.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String CREDITOR = "ROLE_CREDITOR";

    public static final String DEBTOR = "ROLE_DEBTOR";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String SYSTEM = "ROLE_SYSTEM";

    private AuthoritiesConstants() {
    }
}
