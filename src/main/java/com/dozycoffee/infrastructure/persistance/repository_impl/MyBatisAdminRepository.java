package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.admin.application.AdminRepository;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminProfile;
import com.dozycoffee.admin.domain.AdminRole;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.AdminRow;
import com.dozycoffee.infrastructure.persistance.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisAdminRepository implements AdminRepository {

    private final AdminMapper adminMapper;

    @Override
    public void save(Admin admin) throws RepositoryException {
        AdminRow row = RowMapper.toRow(admin);
        adminMapper.upsertAccount(row);
        if (admin.getProfile() != null) {
            adminMapper.upsertProfile(row);
        }
    }

    @Override
    public Optional<Admin> findById(AdminId id) throws RepositoryException {
        return adminMapper.findById(id.getValue()).map(AdminRow::toAdmin);
    }

    @Override
    public Optional<Admin> findByUsername(String username) throws RepositoryException {
        return adminMapper.findByUsername(username).map(AdminRow::toAdmin);
    }

    @Override
    public Optional<Admin> findByRole(AdminRole role) throws RepositoryException {
        return adminMapper.findByRole(role.name().toLowerCase(Locale.ROOT)).map(AdminRow::toAdmin);
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
    }

}
