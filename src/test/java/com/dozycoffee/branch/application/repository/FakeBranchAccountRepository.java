package com.dozycoffee.branch.application.repository;

import com.dozycoffee.branch.application.repository.BranchAccountRepository;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.branch.domain.BranchAccount;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FakeBranchAccountRepository implements BranchAccountRepository {

    private final Map<BranchId, BranchAccount> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public BranchAccount put(BranchAccount account) {
        store.put(account.getId(), account);
        return account;
    }

    public boolean contains(BranchId id) {
        return store.containsKey(id);
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(BranchAccount branchAccount) {
        checkThrow();
        store.put(branchAccount.getId(), branchAccount);
    }

    @Override
    public Optional<BranchAccount> findById(BranchId branchId) {
        checkThrow();
        return Optional.ofNullable(store.get(branchId));
    }

    @Override
    public void deleteById(BranchId branchId) {
        checkThrow();
        store.remove(branchId);
    }

    @Override
    public Optional<BranchAccount> findByBranchCode(BranchCode branchCode) {
        checkThrow();
        return store.values().stream()
                .filter(account -> account.getCode().equals(branchCode))
                .findFirst();
    }
}
