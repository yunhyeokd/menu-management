package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.ProductRow;
import com.dozycoffee.infrastructure.persistance.mapper.ProductMapper;
import com.dozycoffee.catalog.application.repository.ProductRepository;
import com.dozycoffee.catalog.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisProductRepository implements ProductRepository {

    private final ProductMapper productMapper;

    @Override
    public void save(Product product) throws RepositoryException {
        productMapper.upsert(toRow(product));
    }

    @Override
    public boolean existsById(ProductId productId) throws RepositoryException {
        return productMapper.existsById(productId.getValue());
    }

    @Override
    public Optional<Product> findById(ProductId productId) throws RepositoryException {
        return productMapper.findById(productId.getValue()).map(ProductRow::toProduct);
    }

    @Override
    public List<Product> findAllByCategoryId(CategoryId categoryId) throws RepositoryException {
        return productMapper.findAllByCategoryId(categoryId.getValue()).stream()
                .map(ProductRow::toProduct).toList();
    }

    @Override
    public List<Product> findAllActiveCommon() throws RepositoryException {
        return productMapper.findAllActiveCommon().stream()
                .map(ProductRow::toProduct).toList();
    }

    @Override
    public List<Product> findAllActiveBranchExclusive(BranchId branchId) throws RepositoryException {
        return productMapper.findAllActiveBranchExclusive(branchId.getValue()).stream()
                .map(ProductRow::toProduct).toList();
    }

    @Override
    public Optional<Product> findActiveById(ProductId productId) throws RepositoryException {
        return productMapper.findActiveById(productId.getValue()).map(ProductRow::toProduct);
    }

    @Override
    public void updateStatusByBranchId(BranchId branchId, ProductStatus status) throws RepositoryException {
        productMapper.updateStatusByBranchId(branchId.getValue(), status.name());
    }

    @Override
    public void deleteAllByBranchId(BranchId branchId) throws RepositoryException {
        productMapper.deleteAllByBranchId(branchId.getValue());
    }

    @Override
    public void deleteById(ProductId productId) throws RepositoryException {
        productMapper.deleteById(productId.getValue());
    }

    private static ProductRow toRow(Product product) {
        return new ProductRow(
                product.getId().getValue(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCategoryId() != null ? product.getCategoryId().getValue() : null,
                product.getPrice(),
                product.getKcal(),
                product.getAllergenInfo() != null ? product.getAllergenInfo().toString() : null,
                product.getKind().name(),
                product.getBranchId() != null ? product.getBranchId().getValue() : null,
                product.getStatus().name(),
                product.getCreatedAt()
        );
    }
}
