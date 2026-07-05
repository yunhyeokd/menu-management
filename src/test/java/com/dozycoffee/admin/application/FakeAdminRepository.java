package com.dozycoffee.admin.application;

import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminPrincipal;
import com.dozycoffee.admin.domain.SystemAdmin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeAdminRepository implements AdminRepository {

    private final Map<AdminId, AdminPrincipal> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public <T extends AdminPrincipal> T put(T account) {
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
    public void save(AdminPrincipal account) {
        checkThrow();
        store.put(account.getId(), account);
    }

    @Override
    public List<AdminPrincipal> findAll() {
        checkThrow();
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<AdminPrincipal> findById(AdminId id) {
        checkThrow();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Admin> findAdminById(AdminId id) {
        checkThrow();
        return Optional.ofNullable(store.get(id))
                .filter(Admin.class::isInstance)
                .map(Admin.class::cast);
    }

    @Override
    public Optional<AdminPrincipal> findByUsername(String username) {
        checkThrow();
        return store.values().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<SystemAdmin> findSystemAdmin() {
        checkThrow();
        return store.values().stream()
                .filter(SystemAdmin.class::isInstance)
                .map(SystemAdmin.class::cast)
                .findFirst();
    }

    @Override
    public boolean existsByEmployeeNo(String employeeNo) {
        checkThrow();
        return store.values().stream()
                .filter(Admin.class::isInstance)
                .map(Admin.class::cast)
                .anyMatch(a -> employeeNo.equals(a.getProfile().getEmployeeNo()));
    }

    @Override
    public void deleteById(AdminId id) {
        checkThrow();
        store.remove(id);
    }
}
