package com.dozycoffee.application.admin.service;

import com.dozycoffee.application.admin.repository.AdminAccountRepository;
import com.dozycoffee.application.auth.Authenticator;
import com.dozycoffee.application.auth.PasswordHasher;
import com.dozycoffee.domain.admin.AdminAccount;
import com.dozycoffee.domain.admin.AdminStatus;
import com.dozycoffee.domain.auth.Credential;
import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.common.Identifier;

import java.util.Optional;

public class AdminAuthenticator implements Authenticator<Identifier<String>> {

    private final AdminAccountRepository adminAccountRepository;
    private final PasswordHasher passwordHasher;

    public AdminAuthenticator(AdminAccountRepository adminAccountRepository, PasswordHasher passwordHasher) {
        this.adminAccountRepository = adminAccountRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Optional<Principal> authenticate(Identifier<String> username, Credential credential) {
        AdminAccount adminAccount = adminAccountRepository.findByUsername(username.getValue())
                .orElse(null);
        if (adminAccount == null) return Optional.empty();
        if (!adminAccount.isActive()) return Optional.empty();
        if (!passwordHasher.matches(credential.getValue(), adminAccount.getPasswordHash())) {
            return Optional.empty();
        }
        return Optional.of(adminAccount);
    }

}
