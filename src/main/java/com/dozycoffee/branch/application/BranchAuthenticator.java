package com.dozycoffee.branch.application;

import com.dozycoffee.auth.domain.Authenticator;
import com.dozycoffee.auth.application.PasswordHasher;
import com.dozycoffee.auth.domain.Credential;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.branch.domain.BranchAccount;
import com.dozycoffee.branch.domain.BranchCode;

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
