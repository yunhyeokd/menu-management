package com.dozycoffee.admin.application;

import com.dozycoffee.auth.domain.Authenticator;
import com.dozycoffee.auth.application.PasswordHasher;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.auth.domain.Credential;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.core.domain.Identifier;

import java.util.Optional;

public class AdminUsernameAuthenticator implements Authenticator<Identifier<String>> {

    private final AdminRepository adminRepository;
    private final PasswordHasher passwordHasher;

    public AdminUsernameAuthenticator(AdminRepository adminRepository, PasswordHasher passwordHasher) {
        this.adminRepository = adminRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Optional<Principal> authenticate(Identifier<String> username, Credential credential) {
        Admin adminAccount = adminRepository.findByUsername(username.getValue())
                .orElse(null);
        if (adminAccount == null) return Optional.empty();
        if (!adminAccount.isActive()) return Optional.empty();
        if (!passwordHasher.matches(credential.getValue(), adminAccount.getPasswordHash())) {
            return Optional.empty();
        }
        return Optional.of(adminAccount);
    }
}
