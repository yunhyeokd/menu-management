package com.dozycoffee.application.admin.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.admin.AdminAccount;
import com.dozycoffee.domain.admin.AdminId;
import com.dozycoffee.domain.admin.AdminRole;

import java.util.Optional;

public interface AdminAccountRepository {

    void save(AdminAccount newAdminAccount) throws RepositoryException;
    Optional<AdminAccount> findById(AdminId id) throws RepositoryException;
    Optional<AdminAccount> findByUsername(String username) throws RepositoryException;

    Optional<AdminAccount> findByAdminRole(AdminRole adminRole) throws RepositoryException;

    void deleteById(AdminId id);
}
