package com.dozycoffee.admin.application;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FakeAdminRepository implements AdminRepository {

    private final Map<AdminId, Admin> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public Admin put(Admin account) {
        store.put(account.getId(), account);
        return account;
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
    public void save(Admin account) {
        checkThrow();
        store.put(account.getId(), account);
    }

    @Override
    public Optional<Admin> findById(AdminId id) {
        checkThrow();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        checkThrow();
        return store.values().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<Admin> findByRole(AdminRole role) {
        checkThrow();
        return store.values().stream()
                .filter(a -> a.getAdminRole() == role)
                .findFirst();
    }

    @Override
    public boolean existsByEmployeeNo(String employeeNo) {
        checkThrow();
        return store.values().stream()
                .anyMatch(a -> a.getProfile() != null && employeeNo.equals(a.getProfile().getEmployeeNo()));
    }

    @Override
    public void deleteById(AdminId id) {
        checkThrow();
        store.remove(id);
    }
}
