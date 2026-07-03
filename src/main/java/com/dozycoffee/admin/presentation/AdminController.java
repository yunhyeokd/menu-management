package com.dozycoffee.admin.presentation;

import com.dozycoffee.admin.application.AdminService;
import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.presentation.dto.*;
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
    public ResponseEntity<List<AdminResponse>> findAdmins() {
        List<Admin> admins = adminService.findAll();
        List<AdminResponse> response = admins.stream().map(AdminResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<AdminResponse> getAdmin(@PathVariable AdminId adminId) {
        Admin admin = adminService.findById(adminId);
        return ResponseEntity.ok(AdminResponse.from(admin));
    }

    @PostMapping
    public ResponseEntity<AdminResponse> registerAdmin(@RequestBody AdminRegisterRequest request) {
        Admin admin = adminService.registerAdmin(request.toCommand());
        return ResponseEntity.ok(AdminResponse.from(admin));
    }

    @PostMapping("/system")
    public ResponseEntity<AdminResponse> registerSystem(@RequestBody SystemAdminRegisterRequest request) {
        Admin admin = adminService.registerSystem(request.toCommand());
        return ResponseEntity.ok(AdminResponse.from(admin));
    }

    @PatchMapping("/{adminId}/approve")
    public ResponseEntity<Void> approve(@PathVariable AdminId adminId) {
        adminService.approve(adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adminId}/reject")
    public ResponseEntity<Void> reject(@PathVariable AdminId adminId) {
        adminService.reject(adminId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{adminId}/profile")
    public ResponseEntity<AdminResponse> updateProfile(
            @PathVariable AdminId adminId,
            @RequestBody AdminProfileUpdateRequest request
    ) {
        Admin admin = adminService.updateProfile(adminId, request.toCommand());
        return ResponseEntity.ok(AdminResponse.from(admin));
    }

    @PatchMapping("/{adminId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable AdminId adminId,
            @RequestBody AdminPasswordChangeRequest request
    ) {
        adminService.changePassword(adminId, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> softDelete(@PathVariable AdminId adminId) {
        adminService.softDelete(adminId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{adminId}/hard")
    public ResponseEntity<Void> hardDelete(@PathVariable AdminId adminId) {
        adminService.hardDelete(adminId);
        return ResponseEntity.noContent().build();
    }
}
