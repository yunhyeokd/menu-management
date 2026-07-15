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
import com.dozycoffee.catalog.application.dto.TagData;
import com.dozycoffee.catalog.application.repository.ProductQueryRepository;
import com.dozycoffee.catalog.application.repository.ProductRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.application.service.ProductService;
import com.dozycoffee.catalog.domain.Product;
import com.dozycoffee.catalog.domain.ProductId;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class FindSellableProductsUseCase {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;

    public FindSellableProductsUseCase(
            ProductService productService,
            ProductRepository productRepository,
            ProductQueryRepository productQueryRepository
    ) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productQueryRepository = productQueryRepository;
    }

    public List<ProductSnapshot> execute(BranchId branchId) {
        List<Product> products = productService.findSellableProducts(branchId);
        return attachTags(products);
    }

    public List<ProductSnapshot> execute() {
        List<Product> products;
        try {
            products = productRepository.findAllActiveCommon();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
        return attachTags(products);
    }

    public SellableProductDetailResult executeOneDetail(ProductId productId) {
        boolean isSellableCommon = execute().stream().anyMatch(product -> product.id().equals(productId));
        if (!isSellableCommon) {
            throw new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.PRODUCT_NOT_FOUND_ERROR);
        }
        ProductDetailResult detail = productService.findDetailById(productId);
        return SellableProductDetailResult.from(detail, false);
    }

    private List<ProductSnapshot> attachTags(List<Product> products) {
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
