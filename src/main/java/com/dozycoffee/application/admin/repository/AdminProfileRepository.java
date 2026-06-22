package com.dozycoffee.application.admin.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.admin.AdminId;
import com.dozycoffee.domain.admin.AdminProfile;

import java.util.Optional;

public interface AdminProfileRepository {
    void save(AdminProfile newAdminProfile) throws RepositoryException;

    Optional<AdminProfile> findById(AdminId adminId) throws RepositoryException;

    void deleteById(AdminId adminId) throws RepositoryException;
}
