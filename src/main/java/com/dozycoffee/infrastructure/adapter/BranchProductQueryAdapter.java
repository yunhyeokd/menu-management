package com.dozycoffee.infrastructure.adapter;

import com.dozycoffee.branch.application.BranchProductQueryPort;
import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.repository.ProductRepository;
import com.dozycoffee.product.domain.Product;
import com.dozycoffee.product.domain.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BranchProductQueryAdapter implements BranchProductQueryPort {

    private final ProductRepository productRepository;

    @Override
    public Optional<BranchProduct> findById(ProductId productId) throws RepositoryException {
        return productRepository.findById(productId).map(this::toBranchProduct);
    }

    @Override
    public List<BranchProduct> findAllActiveCommon() throws RepositoryException {
        return productRepository.findAllActiveCommon().stream()
                .map(this::toBranchProduct).toList();
    }

    @Override
    public List<BranchProduct> findAllActiveBranchExclusive(BranchId branchId) throws RepositoryException {
        return productRepository.findAllActiveBranchExclusive(branchId).stream()
                .map(this::toBranchProduct).toList();
    }

    private BranchProduct toBranchProduct(Product product) {
        return BranchProduct.of(
                product.getId(),
                product.getBranchId(),
                product.getStatus().name().equals("ACTIVE")
        );
    }
}
