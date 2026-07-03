package com.dozycoffee.branch.presentation;

import com.dozycoffee.branch.application.BranchOperationService;
import com.dozycoffee.branch.application.BranchService;
import com.dozycoffee.branch.application.dto.BranchAuthKeyReissueResult;
import com.dozycoffee.branch.application.dto.BranchCreateResult;
import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.branch.presentation.dto.BranchAuthKeyReissueResponse;
import com.dozycoffee.branch.presentation.dto.BranchCreateRequest;
import com.dozycoffee.branch.presentation.dto.BranchCreateResponse;
import com.dozycoffee.branch.presentation.dto.BranchProductSummaryResponse;
import com.dozycoffee.branch.presentation.dto.BranchProfileResponse;
import com.dozycoffee.branch.presentation.dto.BranchProfileUpdateRequest;
import com.dozycoffee.product.domain.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;
    private final BranchOperationService branchOperationService;

    @PostMapping
    public ResponseEntity<BranchCreateResponse> createBranch(
            @RequestBody BranchCreateRequest request
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
        Branch branch = branchService.find(branchId);
        return ResponseEntity.ok(BranchProfileResponse.from(branch));
    }

    @DeleteMapping("/{branchId}")
    public ResponseEntity<Void> deleteBranch(@PathVariable BranchId branchId) {
        branchService.softDelete(branchId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{branchId}/hard")
    public ResponseEntity<Void> hardDeleteBranch(@PathVariable BranchId branchId) {
        branchService.hardDelete(branchId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{branchId}/profile")
    public ResponseEntity<Void> updateProfile(
            @PathVariable BranchId branchId,
            @RequestBody BranchProfileUpdateRequest request
    ) {
        branchService.updateProfile(branchId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{branchId}/reset-authkey")
    public ResponseEntity<BranchAuthKeyReissueResponse> resetAuthKey(@PathVariable BranchId branchId) {
        BranchAuthKeyReissueResult result = branchService.reissueAuthKey(branchId);
        BranchAuthKeyReissueResponse response = BranchAuthKeyReissueResponse.from(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{branchId}/products")
    public ResponseEntity<List<BranchProductSummaryResponse>> findOverridableProducts(
            @PathVariable BranchId branchId
    ) {
        List<BranchProduct> products = branchOperationService.findOverridableProducts(branchId);
        List<BranchProductSummaryResponse> response = products.stream()
                .map(BranchProductSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{branchId}/products/{productId}/hide")
    public ResponseEntity<Void> hideProduct(
            @PathVariable BranchId branchId,
            @PathVariable ProductId productId
    ) {
        branchOperationService.hideSale(branchId, productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{branchId}/products/{productId}/sold-out")
    public ResponseEntity<Void> soldOutProduct(
            @PathVariable BranchId branchId,
            @PathVariable ProductId productId
    ) {
        branchOperationService.soldOut(branchId, productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{branchId}/products/{productId}/restore-sale")
    public ResponseEntity<Void> restoreProduct(
            @PathVariable BranchId branchId,
            @PathVariable ProductId productId
    ) {
        branchOperationService.restoreSale(branchId, productId);
        return ResponseEntity.noContent().build();
    }

}
