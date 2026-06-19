package com.dozycoffee.application.branch.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.branch.BranchProfile;

import java.util.LinkedHashMap;
import java.util.Map;

public class FakeBranchProfileRepository implements BranchProfileRepository {

    private final Map<BranchId, BranchProfile> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public BranchProfile put(BranchProfile profile) {
        store.put(profile.getBranchId(), profile);
        return profile;
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
    public BranchProfile findById(BranchId branchId) throws RepositoryException {
        checkThrow();
        return store.get(branchId);
    }

    @Override
    public BranchProfile findByName(String name) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(BranchProfile branchProfile) throws RepositoryException {
        checkThrow();
        store.put(branchProfile.getBranchId(), branchProfile);
    }

    @Override
    public void deleteById(BranchId branchId) {
        store.remove(branchId);
    }
}
