package com.dozycoffee.catalog.presentation;

import com.dozycoffee.infrastructure.web.interceptor.RequireRole;
import com.dozycoffee.catalog.application.dto.ProductSnapshot;
import com.dozycoffee.catalog.application.dto.ProductDetailResult;
import com.dozycoffee.catalog.application.dto.ProductSummaryResult;
import com.dozycoffee.catalog.application.service.ProductService;
import com.dozycoffee.catalog.application.usecase.*;
import com.dozycoffee.catalog.domain.*;
import com.dozycoffee.catalog.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
    private final ReplaceProductOptionGroupsUseCase replaceProductOptionGroupsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    @PostMapping("")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<ProductSummaryResponse> registerProduct(@Valid @RequestBody ProductRegisterRequest request) {
        ProductSnapshot product = registerProductUseCase.execute(request.toCommand());
        ProductSummaryResult result = ProductSummaryResult.from(product, product.tags());
        return ResponseEntity.ok(ProductSummaryResponse.from(result));
    }

    @GetMapping("")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<List<ProductSummaryResponse>> searchProducts(@ParameterObject @ModelAttribute ProductSearchRequest request) {
        List<ProductSummaryResult> results = searchProductsUseCase.execute(request.toCommand());
        List<ProductSummaryResponse> response = results.stream().map(ProductSummaryResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable ProductId productId) {
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
