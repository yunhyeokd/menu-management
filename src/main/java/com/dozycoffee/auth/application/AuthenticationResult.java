package com.dozycoffee.auth.application;

import com.dozycoffee.auth.domain.Principal;

public record AuthenticationResult(Principal principal, long sessionTtl) {
}
