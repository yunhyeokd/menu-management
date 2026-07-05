package com.dozycoffee.admin.presentation;

import com.dozycoffee.admin.application.AdminService;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminPrincipal;
import com.dozycoffee.admin.domain.AdminRole;
import com.dozycoffee.admin.presentation.dto.*;
import com.dozycoffee.auth.application.AuthErrors;
import com.dozycoffee.auth.application.AuthServiceCode;
import com.dozycoffee.core.exception.service.AuthorizationException;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.infrastructure.web.interceptor.RequireRole;
import com.dozycoffee.infrastructure.web.resolver.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @RequireRole({"SYSTEM"})
    public ResponseEntity<List<AdminResponse>> findAdmins() {
        List<AdminPrincipal> admins = adminService.findAll();
        List<AdminResponse> response = admins.stream().map(AdminResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<AdminResponse> getMyProfile(@AuthPrincipal Principal principal) {
        AdminPrincipal admin = adminService.findPrincipalById(AdminId.of(principal.getSubject()));
        return ResponseEntity.ok(AdminResponse.from(admin));
    }

    @GetMapping("/{adminId}")
    @RequireRole({"SYSTEM"})
    public ResponseEntity<AdminResponse> getAdmin(@PathVariable AdminId adminId) {
        Admin admin = adminService.findById(adminId);
        return ResponseEntity.ok(AdminResponse.from(admin));
    }

    @PostMapping
    @RequireRole({"ADMIN"})
    public ResponseEntity<AdminResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        Admin admin = adminService.registerAdmin(request.toCommand());
        return ResponseEntity.ok(AdminResponse.from(admin));
    }

    @PatchMapping("/{adminId}/approve")
    @RequireRole({"SYSTEM"})
    public ResponseEntity<Void> approve(@PathVariable AdminId adminId) {
        adminService.approve(adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adminId}/reject")
    @RequireRole({"SYSTEM"})
    public ResponseEntity<Void> reject(@PathVariable AdminId adminId) {
        adminService.reject(adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adminId}/profile")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<AdminResponse> updateProfile(
            @AuthPrincipal Principal principal,
            @PathVariable AdminId adminId,
            @Valid @RequestBody AdminProfileUpdateRequest request
    ) {
        requireSelfOrSystem(principal, adminId);
        Admin admin = adminService.updateProfile(adminId, request.toCommand());
        return ResponseEntity.ok(AdminResponse.from(admin));
    }

    @PatchMapping("/{adminId}/password")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> changePassword(
            @AuthPrincipal Principal principal,
            @PathVariable AdminId adminId,
            @Valid @RequestBody AdminPasswordChangeRequest request
    ) {
        requireSelfOrSystem(principal, adminId);
        adminService.changePassword(adminId, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{adminId}")
    @RequireRole({"SYSTEM"})
    public ResponseEntity<Void> softDelete(@PathVariable AdminId adminId) {
        adminService.softDelete(adminId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{adminId}/hard")
    @RequireRole({"SYSTEM"})
    public ResponseEntity<Void> hardDelete(@PathVariable AdminId adminId) {
        adminService.hardDelete(adminId);
        return ResponseEntity.noContent().build();
    }

    private void requireSelfOrSystem(Principal principal, AdminId adminId) {
        if (principal.getRole().equals(AdminRole.ADMIN.name()) && !principal.getSubject().equals(adminId.getValue())) {
            throw new AuthorizationException(AuthServiceCode.AUTH, AuthErrors.UNAUTHORIZED);
        }
    }
}
