package com.dozycoffee.admin.application;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.admin.domain.AdminAccount;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;

import java.util.Optional;

public interface AdminAccountRepository {

    void save(AdminAccount newAdminAccount) throws RepositoryException;
    Optional<AdminAccount> findById(AdminId id) throws RepositoryException;
    Optional<AdminAccount> findByUsername(String username) throws RepositoryException;

    Optional<AdminAccount> findByAdminRole(AdminRole adminRole) throws RepositoryException;

    void deleteById(AdminId id);
}
