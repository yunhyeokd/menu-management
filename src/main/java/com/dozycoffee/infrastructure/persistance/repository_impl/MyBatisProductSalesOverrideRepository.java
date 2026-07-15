package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.repository.ProductSalesOverrideRepository;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.ProductSalesOverrideRow;
import com.dozycoffee.infrastructure.persistance.mapper.ProductSalesOverrideMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisProductSalesOverrideRepository implements ProductSalesOverrideRepository {

    private final ProductSalesOverrideMapper productSalesOverrideMapper;

    @Override
    public void save(ProductSalesOverride productSalesOverride) throws RepositoryException {
        productSalesOverrideMapper.upsert(toRow(productSalesOverride));
    }

    @Override
    public Optional<ProductSalesOverride> findByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException {
        return productSalesOverrideMapper.findByBranchIdAndProductId(branchId.getValue(), productId.getValue())
                .map(ProductSalesOverrideRow::toProductSalesOverride);
    }

    @Override
    public List<ProductSalesOverride> findAllByBranchId(BranchId branchId) throws RepositoryException {
        return productSalesOverrideMapper.findAllByBranchId(branchId.getValue()).stream()
                .map(ProductSalesOverrideRow::toProductSalesOverride)
                .toList();
    }

    @Override
    public void deleteByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException {
        productSalesOverrideMapper.deleteByBranchIdAndProductId(branchId.getValue(), productId.getValue());
    }

    private static ProductSalesOverrideRow toRow(ProductSalesOverride pso) {
        return new ProductSalesOverrideRow(
                pso.getProductId().getValue(),
                pso.getBranchId().getValue(),
                pso.getStatus().name(),
                pso.getCreatedAt()
        );
    }
}
