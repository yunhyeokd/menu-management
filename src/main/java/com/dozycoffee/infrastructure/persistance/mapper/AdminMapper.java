package com.dozycoffee.infrastructure.persistance.mapper;

import com.dozycoffee.infrastructure.persistance.entity.AdminRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AdminMapper {

    void upsertAccount(AdminRow row);
    void upsertProfile(AdminRow row);
    List<AdminRow> findAll();
    Optional<AdminRow> findById(String adminId);
    Optional<AdminRow> findByIdAndRole(@Param("adminId") String adminId, @Param("role") String role);
    Optional<AdminRow> findByUsername(String username);
    Optional<AdminRow> findByRole(String role);
    boolean existsByEmployeeNo(String employeeNo);
    void deleteAccountById(String adminId);
    void deleteProfileById(String adminId);
}
