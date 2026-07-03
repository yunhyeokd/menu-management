package com.dozycoffee.admin.application;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;

import java.util.List;
import java.util.Optional;

public interface AdminRepository {

    void save(Admin account) throws RepositoryException;
    List<Admin> findAll() throws RepositoryException;
    Optional<Admin> findById(AdminId id) throws RepositoryException;
    Optional<Admin> findByUsername(String username) throws RepositoryException;
    Optional<Admin> findByRole(AdminRole role) throws RepositoryException;
    boolean existsByEmployeeNo(String employeeNo) throws RepositoryException;
    void deleteById(AdminId id) throws RepositoryException;
}
