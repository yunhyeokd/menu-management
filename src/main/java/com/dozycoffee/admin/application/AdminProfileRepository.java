package com.dozycoffee.admin.application;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminProfile;

import java.util.Optional;

public interface AdminProfileRepository {
    void save(AdminProfile newAdminProfile) throws RepositoryException;

    Optional<AdminProfile> findById(AdminId adminId) throws RepositoryException;

    void deleteById(AdminId adminId) throws RepositoryException;
}
