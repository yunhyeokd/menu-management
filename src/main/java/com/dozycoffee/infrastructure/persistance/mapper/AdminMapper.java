package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.AdminRow;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface AdminMapper {

    void upsertAccount(AdminRow row);
    void upsertProfile(AdminRow row);
    Optional<AdminRow> findById(String adminId);
    Optional<AdminRow> findByUsername(String username);
    Optional<AdminRow> findByRole(String role);
    boolean existsByEmployeeNo(String employeeNo);
    void deleteAccountById(String adminId);
    void deleteProfileById(String adminId);
}
