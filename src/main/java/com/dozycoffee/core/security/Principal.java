package com.dozycoffee.core.security;

public interface Principal {
    String getSubject();
    String getRole();
}
