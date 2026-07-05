package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.admin.application.AdminRepository;
import com.dozycoffee.admin.domain.*;
import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.AdminRow;
import com.dozycoffee.infrastructure.persistance.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisAdminRepository implements AdminRepository {

    private final AdminMapper adminMapper;

    @Override
    @Transactional
    public void save(AdminPrincipal adminPrincipal) throws RepositoryException {
        AdminRow row = RowMapper.toRow(adminPrincipal);
        adminMapper.upsertAccount(row);
        if (adminPrincipal instanceof Admin) {
            adminMapper.upsertProfile(row);
        }
    }

    @Override
    public List<AdminPrincipal> findAll() throws RepositoryException {
        return adminMapper.findAll().stream().map(AdminRow::toPrincipal).toList();
    }

    @Override
    public Optional<AdminPrincipal> findById(AdminId id) throws RepositoryException {
        return adminMapper.findById(id.getValue()).map(AdminRow::toPrincipal);
    }

    @Override
    public Optional<Admin> findAdminById(AdminId id) throws RepositoryException {
        return adminMapper.findByIdAndRole(id.getValue(), AdminRole.ADMIN.toString().toLowerCase())
                .map(AdminRow::toAdmin);
    }

    @Override
    public Optional<SystemAdmin> findSystemAdmin() throws RepositoryException {
        return adminMapper
                .findByRole(AdminRole.SYSTEM.toString().toLowerCase())
                .map(AdminRow::toSystemAdmin);
    }

    @Override
    public Optional<AdminPrincipal> findByUsername(String username) throws RepositoryException {
        return adminMapper.findByUsername(username).map(AdminRow::toPrincipal);
    }

    @Override
    public boolean existsByEmployeeNo(String employeeNo) throws RepositoryException {
        return adminMapper.existsByEmployeeNo(employeeNo);
    }

    @Override
    public void deleteById(AdminId id) throws RepositoryException {
        adminMapper.deleteAccountById(id.getValue());
        adminMapper.deleteProfileById(id.getValue());
    }

    public static class RowMapper {

        public static AdminRow toRow(Admin admin) {

            AdminProfile profile = admin.getProfile();

            String employeeNo = null;
            String name = null;
            String phone = null;
            String email = null;

            if (profile != null) {
                employeeNo = profile.getEmployeeNo();
                name = profile.getName();
                phone = profile.getPhone();
                email = profile.getEmail();
            }

            return new AdminRow(
                    admin.getId().getValue(),
                    admin.getAdminRole().toString().toLowerCase(),
                    admin.getStatus().toString().toLowerCase(),
                    admin.getUsername(),
                    admin.getPasswordHash(),
                    admin.getDeletedAt(),
                    admin.getCreatedAt(),
                    employeeNo,
                    name,
                    phone,
                    email
            );
        }

        public static AdminRow toRow(AdminPrincipal adminPrincipal) {
            if (adminPrincipal instanceof Admin admin) {
                return toRow(admin);
            }
            else {
                return new AdminRow(
                        adminPrincipal.getId().getValue(),
                        adminPrincipal.getAdminRole().toString().toLowerCase(),
                        adminPrincipal.getStatus().toString().toLowerCase(),
                        adminPrincipal.getUsername(),
                        adminPrincipal.getPasswordHash(),
                        adminPrincipal.getDeletedAt(),
                        adminPrincipal.getCreatedAt(),
                        null,
                        null,
                        null,
                        null
                );
            }
        }
    }

}
