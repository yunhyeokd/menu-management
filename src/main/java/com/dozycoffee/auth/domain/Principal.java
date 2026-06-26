package com.dozycoffee.domain.auth;

import com.dozycoffee.domain.common.Identifier;

public interface Principal {
    String getSubject();
    String getRole();
}
