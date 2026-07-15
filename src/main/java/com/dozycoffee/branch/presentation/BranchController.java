package com.dozycoffee.branch.presentation;

import com.dozycoffee.branch.application.BranchService;
import com.dozycoffee.branch.application.dto.BranchAuthKeyReissueResult;
import com.dozycoffee.branch.application.dto.BranchCreateResult;
import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.branch.presentation.dto.BranchAuthKeyReissueResponse;
import com.dozycoffee.branch.presentation.dto.BranchCreateRequest;
import com.dozycoffee.branch.presentation.dto.BranchCreateResponse;
import com.dozycoffee.branch.presentation.dto.BranchProfileResponse;
import com.dozycoffee.branch.presentation.dto.BranchProfileUpdateRequest;
import com.dozycoffee.infrastructure.web.interceptor.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<BranchCreateResponse> createBranch(
            @Valid @RequestBody BranchCreateRequest request
    ) {
        BranchCreateResult result = branchService.create(request.toCommand());
        BranchCreateResponse response = BranchCreateResponse.from(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BranchProfileResponse>> findBranches() {
        List<BranchProfileResponse> response = branchService.findAll().stream()
                .map(BranchProfileResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<BranchProfileResponse> getBranch(@PathVariable BranchId branchId) {
        Branch branch = branchService.findById(branchId);
        return ResponseEntity.ok(BranchProfileResponse.from(branch));
    }

    @DeleteMapping("/{branchId}")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> deleteBranch(@PathVariable BranchId branchId) {
        branchService.softDelete(branchId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{branchId}/hard")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> hardDeleteBranch(@PathVariable BranchId branchId) {
        branchService.hardDelete(branchId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{branchId}/profile")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> updateProfile(
            @PathVariable BranchId branchId,
            @Valid @RequestBody BranchProfileUpdateRequest request
    ) {
        branchService.updateProfile(branchId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{branchId}/reset-authkey")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<BranchAuthKeyReissueResponse> resetAuthKey(@PathVariable BranchId branchId) {
        BranchAuthKeyReissueResult result = branchService.reissueAuthKey(branchId);
        BranchAuthKeyReissueResponse response = BranchAuthKeyReissueResponse.from(result);
        return ResponseEntity.ok(response);
    }

}
