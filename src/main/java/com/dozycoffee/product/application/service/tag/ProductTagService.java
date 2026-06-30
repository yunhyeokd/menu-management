package com.dozycoffee.product.application.service.tag;

import org.springframework.stereotype.Service;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.core.application.exception.ResourceNotFoundException;
import com.dozycoffee.core.application.exception.SystemException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.repository.ProductTagRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductTag;
import com.dozycoffee.product.domain.TagId;

import java.util.List;
import java.util.Set;

@Service
public class ProductTagService {

    private final ProductTagRepository productTagRepository;
    private final TagService tagService;

    public ProductTagService(ProductTagRepository productTagRepository, TagService tagService) {
        this.productTagRepository = productTagRepository;
        this.tagService = tagService;
    }

    public List<TagData> saveTags(ProductId productId, Set<String> tagNames) {
        try {
            List<TagData> tags = tagNames.stream()
                    .map(tagService::findOrCreate)
                    .map(TagData::from)
                    .toList();
            for (TagData tag : tags) {
                productTagRepository.save(ProductTag.create(productId, tag.id()));
            }
            return tags;
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<TagData> replaceTags(ProductId productId, Set<String> tagNames) {
        try {
            productTagRepository.deleteAllByProductId(productId);
            return saveTags(productId, tagNames);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void deleteAllByProductId(ProductId productId) {
        try {
            productTagRepository.deleteAllByProductId(productId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<ProductId> findLinkedProductIds(TagId tagId) {
        try {
            return productTagRepository.findAllByTagId(tagId).stream()
                    .map(ProductTag::getProductId)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void deleteAllByTagId(TagId tagId) {
        try {
            productTagRepository.deleteAllByTagId(tagId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}
