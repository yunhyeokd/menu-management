package com.dozycoffee.branch.application;

import org.springframework.stereotype.Service;

import com.dozycoffee.core.security.Authenticator;
import com.dozycoffee.core.security.PasswordHasher;
import com.dozycoffee.core.security.Credential;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchCode;

import java.util.Optional;

@Service
public class BranchAuthenticator implements Authenticator<BranchCode> {

    private final BranchRepository branchRepository;
    private final PasswordHasher passwordHasher;

    public BranchAuthenticator(BranchRepository branchRepository, PasswordHasher passwordHasher) {
        this.branchRepository = branchRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Optional<Principal> authenticate(BranchCode branchCode, Credential credential) {
        Branch branch = branchRepository.findByCode(branchCode).orElse(null);
        if (branch == null || !branch.isActive()) return Optional.empty();
        if (!passwordHasher.matches(credential.getValue(), branch.getAuthKeyHash())) {
            return Optional.empty();
        }
        return Optional.of(branch);
    }
}
