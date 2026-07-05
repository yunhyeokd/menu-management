package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.ProductTagRow;
import com.dozycoffee.infrastructure.persistance.entity.TagRow;
import com.dozycoffee.infrastructure.persistance.mapper.ProductTagMapper;
import com.dozycoffee.product.application.repository.ProductTagRepository;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductTag;
import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyBatisProductTagRepository implements ProductTagRepository {

    private final ProductTagMapper productTagMapper;

    @Override
    public void save(ProductTag productTag) throws RepositoryException {
        productTagMapper.insert(toRow(productTag));
    }

    @Override
    public void deleteAllByTagId(TagId tagId) throws RepositoryException {
        productTagMapper.deleteAllByTagId(tagId.getValue());
    }

    @Override
    public void deleteAllByProductId(ProductId productId) throws RepositoryException {
        productTagMapper.deleteAllByProductId(productId.getValue());
    }

    @Override
    public List<ProductTag> findAllByTagId(TagId tagId) throws RepositoryException {
        return productTagMapper.findAllByTagId(tagId.getValue()).stream()
                .map(ProductTagRow::toProductTag).toList();
    }

    @Override
    public List<Tag> findTagsByProductId(ProductId productId) throws RepositoryException {
        return productTagMapper.findTagsByProductId(productId.getValue()).stream()
                .map(TagRow::toTag).toList();
    }

    private static ProductTagRow toRow(ProductTag productTag) {
        return new ProductTagRow(
                productTag.getProductId().getValue(),
                productTag.getTagId().getValue(),
                productTag.getCreatedAt()
        );
    }
}
