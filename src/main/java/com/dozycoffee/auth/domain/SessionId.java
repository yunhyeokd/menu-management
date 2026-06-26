package com.dozycoffee.auth.domain;

import com.dozycoffee.core.domain.StringIdentifier;

public class SessionId extends StringIdentifier {
    protected SessionId(String value) {
        super(value);
    }

    public static SessionId of(String value) {
        return new SessionId(value);
    }
}
