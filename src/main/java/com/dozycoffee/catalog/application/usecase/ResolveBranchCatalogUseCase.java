package com.dozycoffee.catalog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.catalog.application.ProductServiceCode;
import com.dozycoffee.catalog.application.dto.ProductDetailResult;
import com.dozycoffee.catalog.application.dto.ProductSnapshot;
import com.dozycoffee.catalog.application.dto.SellableProductDetailResult;
import com.dozycoffee.catalog.application.dto.SellableProductResult;
import com.dozycoffee.catalog.application.repository.BranchExistencePort;
import com.dozycoffee.catalog.application.repository.ProductSalesOverrideRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.application.service.ProductService;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.catalog.domain.override.ProductSalesOverrideStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ResolveBranchCatalogUseCase {

    private final FindSellableProductsUseCase findSellableProductsUseCase;
    private final ProductService productService;
    private final ProductSalesOverrideRepository productSalesOverrideRepository;
    private final BranchExistencePort branchExistencePort;

    public ResolveBranchCatalogUseCase(
            FindSellableProductsUseCase findSellableProductsUseCase,
            ProductService productService,
            ProductSalesOverrideRepository productSalesOverrideRepository,
            BranchExistencePort branchExistencePort
    ) {
        this.findSellableProductsUseCase = findSellableProductsUseCase;
        this.productService = productService;
        this.productSalesOverrideRepository = productSalesOverrideRepository;
        this.branchExistencePort = branchExistencePort;
    }

    public List<SellableProductResult> execute(BranchId branchId) {
        assertBranchExists(branchId);
        List<ProductSnapshot> products = findSellableProductsUseCase.execute(branchId);
        Map<ProductId, ProductSalesOverrideStatus> statusByProductId = fetchOverrideStatuses(branchId);
        return products.stream()
                .filter(product -> statusByProductId.get(product.id()) != ProductSalesOverrideStatus.HIDDEN)
                .map(product -> SellableProductResult.from(product, statusByProductId.get(product.id()) == ProductSalesOverrideStatus.SOLD_OUT))
                .toList();
    }

    public SellableProductDetailResult execute(BranchId branchId, ProductId productId) {
        assertBranchExists(branchId);
        List<ProductSnapshot> products = findSellableProductsUseCase.execute(branchId);
        Map<ProductId, ProductSalesOverrideStatus> statusByProductId = fetchOverrideStatuses(branchId);

        boolean eligible = products.stream().anyMatch(product -> product.id().equals(productId))
                && statusByProductId.get(productId) != ProductSalesOverrideStatus.HIDDEN;
        if (!eligible) {
            throw new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.PRODUCT_NOT_FOUND_ERROR);
        }

        ProductDetailResult detail = productService.findDetailById(productId);
        boolean soldOut = statusByProductId.get(productId) == ProductSalesOverrideStatus.SOLD_OUT;
        return SellableProductDetailResult.from(detail, soldOut);
    }

    private void assertBranchExists(BranchId branchId) {
        try {
            if (!branchExistencePort.existsById(branchId)) {
                throw new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.BRANCH_NOT_FOUND_ERROR);
            }
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    private Map<ProductId, ProductSalesOverrideStatus> fetchOverrideStatuses(BranchId branchId) {
        try {
            return productSalesOverrideRepository.findAllByBranchId(branchId).stream()
                    .collect(Collectors.toMap(ProductSalesOverride::getProductId, ProductSalesOverride::getStatus));
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}
