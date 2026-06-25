package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.auth.Authenticator;
import com.dozycoffee.application.auth.PasswordHasher;
import com.dozycoffee.application.branch.repository.BranchAccountRepository;
import com.dozycoffee.domain.auth.Credential;
import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.branch.BranchAccount;
import com.dozycoffee.domain.branch.BranchCode;

import java.util.Optional;

public class BranchAuthenticator implements Authenticator<BranchCode> {

    private final BranchAccountRepository branchAccountRepository;
    private final PasswordHasher passwordHasher;

    public BranchAuthenticator(BranchAccountRepository branchAccountRepository, PasswordHasher passwordHasher) {
        this.branchAccountRepository = branchAccountRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Optional<Principal> authenticate(BranchCode branchCode, Credential credential) {
        BranchAccount branchAccount = branchAccountRepository.findByBranchCode(branchCode)
                .orElse(null);
        if (branchAccount == null || !branchAccount.isActive()) return Optional.empty();
        if (!passwordHasher.matches(credential.getValue(), branchAccount.getAuthKeyHash())) {
            return Optional.empty();
        }
        return Optional.of(branchAccount);
    }

}
