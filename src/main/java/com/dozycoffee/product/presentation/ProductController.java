package com.dozycoffee.product.presentation;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.infrastructure.web.interceptor.RequireRole;
import com.dozycoffee.product.application.dto.ProductSnapshot;
import com.dozycoffee.product.application.dto.ProductDetailResult;
import com.dozycoffee.product.application.dto.ProductSummaryResult;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.application.usecase.*;
import com.dozycoffee.product.domain.*;
import com.dozycoffee.product.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RegisterProductUseCase registerProductUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final UpdateProductProfileUseCase updateProductProfileUseCase;
    private final FindSellableProductsUseCase findSellableProductsUseCase;
    private final ReplaceProductOptionGroupsUseCase replaceProductOptionGroupsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    @PostMapping("")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<ProductSummaryResponse> registerProduct(@Valid @RequestBody ProductRegisterRequest request) {
        ProductSnapshot product = registerProductUseCase.execute(request.toCommand());
        ProductSummaryResult result = ProductSummaryResult.from(product, product.tags());
        return ResponseEntity.ok(ProductSummaryResponse.from(result));
    }

    @GetMapping("/search")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<List<ProductSummaryResponse>> searchProducts(@ModelAttribute ProductSearchRequest request) {
        List<ProductSummaryResult> results = searchProductsUseCase.execute(request.toCommand());
        List<ProductSummaryResponse> response = results.stream().map(ProductSummaryResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/{productId}")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable ProductId productId) {
        ProductDetailResult result = productService.findDetailById(productId);
        return ResponseEntity.ok(ProductDetailResponse.from(result));
    }

    @GetMapping("/sellable")
    @RequireRole({"SYSTEM", "ADMIN", "BRANCH"})
    public ResponseEntity<List<ProductSummaryResponse>> findSellableProducts(@RequestParam BranchId branchId) {
        List<ProductSnapshot> products = findSellableProductsUseCase.execute(branchId);
        List<ProductSummaryResponse> response = products.stream()
                .map(product -> ProductSummaryResult.from(product, product.tags()))
                .map(ProductSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sellable/{productId}")
    @RequireRole({"SYSTEM", "ADMIN", "BRANCH"})
    public ResponseEntity<ProductDetailResponse> getSellableProduct(@PathVariable ProductId productId) {
        productService.findSellableProductById(productId);
        ProductDetailResult result = productService.findDetailById(productId);
        return ResponseEntity.ok(ProductDetailResponse.from(result));
    }

    @PatchMapping("/{productId}")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> modifyProduct(@PathVariable ProductId productId, @Valid @RequestBody ProductModifyRequest request) {
        updateProductProfileUseCase.execute(productId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/activate")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> activateProduct(@PathVariable ProductId productId) {
        productService.activate(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/deactivate")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> deactivateProduct(@PathVariable ProductId productId) {
        productService.deactivate(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/options")
    @RequireRole({"ADMIN"})
    public ResponseEntity<Void> replaceOptions(@PathVariable ProductId productId, @Valid @RequestBody ProductOptionsReplaceRequest request) {
        replaceProductOptionGroupsUseCase.execute(productId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> deleteProduct(@PathVariable ProductId productId) {
        deleteProductUseCase.execute(productId);
        return ResponseEntity.noContent().build();
    }

}
