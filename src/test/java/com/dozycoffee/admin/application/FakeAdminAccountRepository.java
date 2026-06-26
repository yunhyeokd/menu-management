package com.dozycoffee.admin.application;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.admin.domain.AdminAccount;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FakeAdminAccountRepository implements AdminAccountRepository {

    private final Map<AdminId, AdminAccount> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public AdminAccount put(AdminAccount account) {
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
    public void save(AdminAccount newAdminAccount) {
        checkThrow();
        store.put(newAdminAccount.getId(), newAdminAccount);
    }

    @Override
    public Optional<AdminAccount> findById(AdminId id) {
        checkThrow();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<AdminAccount> findByUsername(String username) {
        checkThrow();
        return store.values().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<AdminAccount> findByAdminRole(AdminRole adminRole) {
        checkThrow();
        return store.values().stream()
                .filter(a -> a.getAdminRole() == adminRole)
                .findFirst();
    }

    @Override
    public void deleteById(AdminId id) {
        checkThrow();
        store.remove(id);
    }
}
