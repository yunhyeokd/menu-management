package com.dozycoffee.catalog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.catalog.application.ProductServiceCode;
import com.dozycoffee.catalog.application.dto.ProductOverrideResult;
import com.dozycoffee.catalog.application.dto.ProductSnapshot;
import com.dozycoffee.catalog.application.repository.BranchExistencePort;
import com.dozycoffee.catalog.application.repository.ProductSalesOverrideRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.catalog.domain.override.ProductSalesOverrideStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FindProductOverridesUseCase {

    private final FindSellableProductsUseCase findSellableProductsUseCase;
    private final ProductSalesOverrideRepository productSalesOverrideRepository;
    private final BranchExistencePort branchExistencePort;

    public FindProductOverridesUseCase(
            FindSellableProductsUseCase findSellableProductsUseCase,
            ProductSalesOverrideRepository productSalesOverrideRepository,
            BranchExistencePort branchExistencePort
    ) {
        this.findSellableProductsUseCase = findSellableProductsUseCase;
        this.productSalesOverrideRepository = productSalesOverrideRepository;
        this.branchExistencePort = branchExistencePort;
    }

    public List<ProductOverrideResult> execute(BranchId branchId) {
        assertBranchExists(branchId);
        List<ProductSnapshot> products = findSellableProductsUseCase.execute(branchId);
        Map<ProductId, ProductSalesOverrideStatus> statusByProductId = fetchOverrideStatuses(branchId);
        return products.stream()
                .map(product -> ProductOverrideResult.from(product, statusByProductId.get(product.id())))
                .toList();
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
