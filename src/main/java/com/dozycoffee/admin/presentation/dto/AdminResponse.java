package com.dozycoffee.admin.presentation.dto;

import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminProfile;

import java.time.Instant;

public record AdminResponse(
        String adminId,
        String role,
        String username,
        String status,
        String employeeNo,
        String name,
        String phone,
        String email,
        Instant createdAt
) {

    public static AdminResponse from(Admin admin) {
        AdminProfile profile = admin.getProfile();
        return new AdminResponse(
                admin.getId().getValue(),
                admin.getAdminRole().name(),
                admin.getUsername(),
                admin.getStatus().name(),
                profile != null ? profile.getEmployeeNo() : null,
                profile != null ? profile.getName() : null,
                profile != null ? profile.getPhone() : null,
                profile != null ? profile.getEmail() : null,
                admin.getCreatedAt()
        );
    }
}
