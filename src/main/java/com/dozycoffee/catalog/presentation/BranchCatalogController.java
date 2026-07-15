package com.dozycoffee.catalog.presentation;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.dto.ProductOverrideResult;
import com.dozycoffee.catalog.application.dto.SellableProductDetailResult;
import com.dozycoffee.catalog.application.dto.SellableProductResult;
import com.dozycoffee.catalog.application.service.override.ProductOverrideService;
import com.dozycoffee.catalog.application.usecase.FindProductOverridesUseCase;
import com.dozycoffee.catalog.application.usecase.ResolveBranchCatalogUseCase;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.presentation.dto.ProductOverrideResponse;
import com.dozycoffee.catalog.presentation.dto.SellableProductDetailResponse;
import com.dozycoffee.catalog.presentation.dto.SellableProductResponse;
import com.dozycoffee.infrastructure.web.interceptor.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/branches/{branchId}/catalog")
@RequiredArgsConstructor
public class BranchCatalogController {

    private final ResolveBranchCatalogUseCase resolveBranchCatalogUseCase;
    private final FindProductOverridesUseCase findProductOverridesUseCase;
    private final ProductOverrideService productOverrideService;

    @GetMapping
    public ResponseEntity<List<SellableProductResponse>> resolveCatalog(@PathVariable BranchId branchId) {
        List<SellableProductResult> results = resolveBranchCatalogUseCase.execute(branchId);
        List<SellableProductResponse> response = results.stream().map(SellableProductResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<SellableProductDetailResponse> resolveCatalogProduct(
            @PathVariable BranchId branchId,
            @PathVariable ProductId productId
    ) {
        SellableProductDetailResult result = resolveBranchCatalogUseCase.execute(branchId, productId);
        return ResponseEntity.ok(SellableProductDetailResponse.from(result));
    }

    @GetMapping("/overrides")
    @RequireRole({"SYSTEM", "BRANCH"})
    public ResponseEntity<List<ProductOverrideResponse>> findOverrides(@PathVariable BranchId branchId) {
        List<ProductOverrideResult> results = findProductOverridesUseCase.execute(branchId);
        List<ProductOverrideResponse> response = results.stream().map(ProductOverrideResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/overrides/{productId}/hide")
    @RequireRole({"SYSTEM", "BRANCH"})
    public ResponseEntity<Void> hide(@PathVariable BranchId branchId, @PathVariable ProductId productId) {
        productOverrideService.hideSale(branchId, productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/overrides/{productId}/sold-out")
    @RequireRole({"SYSTEM", "BRANCH"})
    public ResponseEntity<Void> soldOut(@PathVariable BranchId branchId, @PathVariable ProductId productId) {
        productOverrideService.soldOut(branchId, productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/overrides/{productId}/restore-sale")
    @RequireRole({"SYSTEM", "BRANCH"})
    public ResponseEntity<Void> restoreSale(@PathVariable BranchId branchId, @PathVariable ProductId productId) {
        productOverrideService.restoreSale(branchId, productId);
        return ResponseEntity.noContent().build();
    }
}
