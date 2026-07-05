package com.dozycoffee.product.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.product.application.dto.ProductSnapshot;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.repository.ProductQueryRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.application.service.ProductService;
import com.dozycoffee.product.domain.Product;
import com.dozycoffee.product.domain.ProductId;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class FindSellableProductsUseCase {

    private final ProductService productService;
    private final ProductQueryRepository productQueryRepository;

    public FindSellableProductsUseCase(ProductService productService, ProductQueryRepository productQueryRepository) {
        this.productService = productService;
        this.productQueryRepository = productQueryRepository;
    }

    public List<ProductSnapshot> execute(BranchId branchId) {
        List<Product> products = productService.findSellableProducts(branchId);
        if (products.isEmpty()) return List.of();

        List<ProductId> productIds = products.stream().map(Product::getId).toList();
        Map<ProductId, List<TagData>> tagsByProductId;
        try {
            tagsByProductId = productQueryRepository.findTagsByProductIds(productIds);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }

        return products.stream()
                .map(product -> ProductSnapshot.from(product, tagsByProductId.getOrDefault(product.getId(), List.of())))
                .toList();
    }
}
