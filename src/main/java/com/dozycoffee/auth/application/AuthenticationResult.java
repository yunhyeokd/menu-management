package com.dozycoffee.auth.application;

import com.dozycoffee.core.security.Principal;

public record AuthenticationResult(Principal principal, long sessionTtl) {
}
