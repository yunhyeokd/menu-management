package com.dozycoffee.admin.presentation.dto;

import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminPrincipal;
import com.dozycoffee.admin.domain.AdminProfile;
import com.dozycoffee.admin.domain.SystemAdmin;

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

    private static AdminResponse from(Admin admin) {
        AdminProfile profile = admin.getProfile();
        return new AdminResponse(
                admin.getId().getValue(),
                admin.getAdminRole().name(),
                admin.getUsername(),
                admin.getStatus().name(),
                profile.getEmployeeNo(),
                profile.getName(),
                profile.getPhone(),
                profile.getEmail(),
                admin.getCreatedAt()
        );
    }

    public static AdminResponse from(AdminPrincipal principal) {
        if (principal instanceof Admin admin) return from(admin);
        else return new AdminResponse(
                principal.getId().getValue(),
                principal.getAdminRole().name(),
                principal.getUsername(),
                principal.getStatus().name(),
                null,
                null,
                null,
                null,
                principal.getCreatedAt()
        );
    }
}
