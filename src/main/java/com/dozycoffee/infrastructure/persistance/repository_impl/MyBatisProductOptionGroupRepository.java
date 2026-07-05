package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.ProductOptionGroupRow;
import com.dozycoffee.infrastructure.persistance.mapper.ProductOptionGroupMapper;
import com.dozycoffee.product.application.repository.ProductOptionGroupRepository;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductOptionGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisProductOptionGroupRepository implements ProductOptionGroupRepository {

    private final ProductOptionGroupMapper productOptionGroupMapper;

    @Override
    public void save(ProductOptionGroup productOptionGroup) throws RepositoryException {
        productOptionGroupMapper.upsert(toRow(productOptionGroup));
    }

    @Override
    public List<ProductOptionGroup> findAllByOptionGroupId(OptionGroupId id) throws RepositoryException {
        return productOptionGroupMapper.findAllByOptionGroupId(id.getValue()).stream()
                .map(ProductOptionGroupRow::toProductOptionGroup).toList();
    }

    @Override
    public Optional<ProductOptionGroup> findByProductIdAndOptionGroupId(ProductId productId, OptionGroupId optionGroupId) throws RepositoryException {
        return productOptionGroupMapper.findByProductIdAndOptionGroupId(productId.getValue(), optionGroupId.getValue())
                .map(ProductOptionGroupRow::toProductOptionGroup);
    }

    @Override
    public void deleteAllByProductId(ProductId productId) throws RepositoryException {
        productOptionGroupMapper.deleteAllByProductId(productId.getValue());
    }

    private static ProductOptionGroupRow toRow(ProductOptionGroup pog) {
        return new ProductOptionGroupRow(
                pog.getProductId().getValue(),
                pog.getOptionGroupId().getValue(),
                pog.isRequired(),
                pog.isAllowMultiple(),
                pog.getCreatedAt()
        );
    }
}
