package com.dozycoffee.catalog.presentation;

import com.dozycoffee.catalog.application.dto.ProductSnapshot;
import com.dozycoffee.catalog.application.dto.SellableProductDetailResult;
import com.dozycoffee.catalog.application.dto.SellableProductResult;
import com.dozycoffee.catalog.application.usecase.FindSellableProductsUseCase;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.presentation.dto.SellableProductDetailResponse;
import com.dozycoffee.catalog.presentation.dto.SellableProductResponse;
import com.dozycoffee.infrastructure.web.interceptor.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final FindSellableProductsUseCase findSellableProductsUseCase;

    @GetMapping
    @RequireRole({"SYSTEM", "ADMIN", "BRANCH"})
    public ResponseEntity<List<SellableProductResponse>> findCatalog() {
        List<ProductSnapshot> products = findSellableProductsUseCase.execute();
        List<SellableProductResponse> response = products.stream()
                .map(product -> SellableProductResult.from(product, false))
                .map(SellableProductResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    @RequireRole({"SYSTEM", "ADMIN", "BRANCH"})
    public ResponseEntity<SellableProductDetailResponse> getCatalogProduct(@PathVariable ProductId productId) {
        SellableProductDetailResult result = findSellableProductsUseCase.executeOneDetail(productId);
        return ResponseEntity.ok(SellableProductDetailResponse.from(result));
    }
}
