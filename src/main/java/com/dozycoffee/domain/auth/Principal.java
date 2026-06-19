package com.dozycoffee.domain.auth;

import com.dozycoffee.domain.common.Identifier;

public interface Principal {
    Identifier<?> getId();
    String getRole();
}
