package com.dozycoffee.application.admin.repository;

import com.dozycoffee.admin.application.repository.AdminProfileRepository;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminProfile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FakeAdminProfileRepository implements AdminProfileRepository {

    private final Map<AdminId, AdminProfile> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public AdminProfile put(AdminProfile profile) {
        store.put(profile.getAdminId(), profile);
        return profile;
    }

    public boolean contains(AdminId id) {
        return store.containsKey(id);
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(AdminProfile newAdminProfile) {
        checkThrow();
        store.put(newAdminProfile.getAdminId(), newAdminProfile);
    }

    @Override
    public Optional<AdminProfile> findById(AdminId adminId) {
        checkThrow();
        return Optional.ofNullable(store.get(adminId));
    }

    @Override
    public void deleteById(AdminId adminId) {
        checkThrow();
        store.remove(adminId);
    }
}
