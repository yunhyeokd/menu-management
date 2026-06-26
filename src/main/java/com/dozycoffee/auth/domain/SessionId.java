package com.dozycoffee.domain.auth;

import com.dozycoffee.domain.common.StringIdentifier;

public class SessionId extends StringIdentifier {
    protected SessionId(String value) {
        super(value);
    }

    public static SessionId of(String value) {
        return new SessionId(value);
    }
}
