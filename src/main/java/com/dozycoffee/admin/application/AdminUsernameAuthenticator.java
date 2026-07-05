package com.dozycoffee.admin.application;

import org.springframework.stereotype.Service;

import com.dozycoffee.core.security.Authenticator;
import com.dozycoffee.core.security.PasswordHasher;
import com.dozycoffee.admin.domain.AdminPrincipal;
import com.dozycoffee.core.security.Credential;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.id.Identifier;

import java.util.Optional;

@Service
public class AdminUsernameAuthenticator implements Authenticator<Identifier<String>> {

    private final AdminRepository adminRepository;
    private final PasswordHasher passwordHasher;

    public AdminUsernameAuthenticator(AdminRepository adminRepository, PasswordHasher passwordHasher) {
        this.adminRepository = adminRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Optional<Principal> authenticate(Identifier<String> username, Credential credential) {
        AdminPrincipal adminPrincipal = adminRepository.findByUsername(username.getValue())
                .orElse(null);
        if (adminPrincipal == null) return Optional.empty();
        if (!adminPrincipal.isActive()) return Optional.empty();
        if (!passwordHasher.matches(credential.getValue(), adminPrincipal.getPasswordHash())) {
            return Optional.empty();
        }
        return Optional.of(adminPrincipal);
    }
}
