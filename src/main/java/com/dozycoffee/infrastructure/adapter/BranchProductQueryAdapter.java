package com.dozycoffee.infrastructure.adapter;

import com.dozycoffee.branch.application.BranchProductQueryPort;
import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.product.application.dto.ProductSnapshot;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.repository.ProductRepository;
import com.dozycoffee.product.application.usecase.FindSellableProductsUseCase;
import com.dozycoffee.product.domain.Product;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BranchProductQueryAdapter implements BranchProductQueryPort {

    private final ProductRepository productRepository;
    private final FindSellableProductsUseCase findSellableProductsUseCase;

    @Override
    public Optional<BranchProduct> findById(ProductId productId) throws RepositoryException {
        return productRepository.findById(productId).map(this::toBranchProduct);
    }

    @Override
    public List<BranchProduct> findOverridableProducts(BranchId branchId) throws RepositoryException {
        return findSellableProductsUseCase.execute(branchId).stream()
                .map(this::toBranchProduct)
                .toList();
    }

    private BranchProduct toBranchProduct(Product product) {
        return BranchProduct.of(
                product.getId(),
                product.getBranchId(),
                product.getStatus() == ProductStatus.ACTIVE
        );
    }

    private BranchProduct toBranchProduct(ProductSnapshot data) {
        return BranchProduct.of(
                data.id(),
                data.branchId(),
                data.status() == ProductStatus.ACTIVE,
                data.name(),
                data.price(),
                data.imageUrl(),
                data.tags().stream().map(TagData::name).toList(),
                data.categoryId()
        );
    }
}
