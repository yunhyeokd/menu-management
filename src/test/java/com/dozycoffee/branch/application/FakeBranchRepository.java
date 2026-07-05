package com.dozycoffee.branch.application;

import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.base.RepositoryException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeBranchRepository implements BranchRepository {

    private final Map<BranchId, Branch> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public Branch put(Branch branch) {
        store.put(branch.getId(), branch);
        return branch;
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
    public void save(Branch branch) {
        checkThrow();
        store.put(branch.getId(), branch);
    }

    @Override
    public Optional<Branch> findById(BranchId branchId) {
        checkThrow();
        return Optional.ofNullable(store.get(branchId));
    }

    @Override
    public Optional<Branch> findByCode(BranchCode code) {
        checkThrow();
        return store.values().stream()
                .filter(b -> b.getCode().equals(code))
                .findFirst();
    }

    @Override
    public Optional<Branch> findByName(String name) {
        checkThrow();
        return store.values().stream()
                .filter(b -> b.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Branch> findAll() {
        checkThrow();
        return List.copyOf(store.values());
    }

    @Override
    public void deleteById(BranchId branchId) {
        checkThrow();
        store.remove(branchId);
    }
}
