package com.dozycoffee.auth.domain;

public interface Principal {
    String getSubject();
    String getRole();
}
