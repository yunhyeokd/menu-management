package com.dozycoffee.core.session;

import com.dozycoffee.core.id.StringIdentifier;

public class SessionId extends StringIdentifier {
    protected SessionId(String value) {
        super(value);
    }

    public static SessionId of(String value) {
        return new SessionId(value);
    }
}
