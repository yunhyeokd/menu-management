package com.dozycoffee.admin.application;

import com.dozycoffee.admin.domain.AdminPrincipal;
import com.dozycoffee.admin.domain.SystemAdmin;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminId;

import java.util.List;
import java.util.Optional;

public interface AdminRepository {

    void save(AdminPrincipal adminPrincipal) throws RepositoryException;
    List<AdminPrincipal> findAll() throws RepositoryException;
    Optional<AdminPrincipal> findById(AdminId id) throws RepositoryException;
    Optional<AdminPrincipal> findByUsername(String username) throws RepositoryException;
    Optional<Admin> findAdminById(AdminId id) throws RepositoryException;
    Optional<SystemAdmin> findSystemAdmin() throws RepositoryException;
    boolean existsByEmployeeNo(String employeeNo) throws RepositoryException;
    void deleteById(AdminId id) throws RepositoryException;
}
