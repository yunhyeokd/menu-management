package com.dozycoffee.application.product.service.tag;

import com.dozycoffee.application.common.IdentifierGenerator;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.dto.TagData;
import com.dozycoffee.application.product.repository.ProductTagRepository;
import com.dozycoffee.application.product.repository.TagRepository;
import com.dozycoffee.application.product.service.ProductBusinessException;
import com.dozycoffee.application.product.service.ProductErrors;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductException;
import com.dozycoffee.domain.product.ProductTag;
import com.dozycoffee.domain.product.Tag;
import com.dozycoffee.domain.product.TagId;

import java.util.List;
import java.util.stream.Collectors;

public class TagService {

    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final IdentifierGenerator<TagId> idGenerator;

    public TagService(
            TagRepository tagRepository,
            ProductTagRepository productTagRepository,
            IdentifierGenerator<TagId> idGenerator
    ) {
        this.tagRepository = tagRepository;
        this.productTagRepository = productTagRepository;
        this.idGenerator = idGenerator;
    }

    public TagData create(String tagName) {
        try {
            if (tagRepository.findByName(tagName).isPresent()) {
                throw ProductBusinessException.of(ProductErrors.DUPLICATE_TAG_NAME_ERROR);
            }
            try {
                Tag newTag = Tag.create(idGenerator.generate(), tagName);
                tagRepository.save(newTag);
                return TagData.from(newTag);
            } catch (ProductException e) {
                throw ProductBusinessException.of(ProductErrors.INVALID_TAG_ERROR);
            }
        } catch (RepositoryException e) {
            throw ProductBusinessException.of(ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void changeTagName(TagId tagId, String newTagName) {
        try {
            if (tagRepository.findByName(newTagName).isPresent()) {
                throw ProductBusinessException.of(ProductErrors.DUPLICATE_TAG_NAME_ERROR);
            }
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> ProductBusinessException.of(ProductErrors.TAG_NOT_FOUND_ERROR));
            try {
                tag.updateName(newTagName);
            } catch (ProductException e) {
                throw ProductBusinessException.of(ProductErrors.INVALID_TAG_ERROR);
            }
            tagRepository.save(tag);
        } catch (RepositoryException e) {
            throw ProductBusinessException.of(ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<TagData> findAll() {
        try {
            return tagRepository.findAll().stream()
                    .map(TagData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw ProductBusinessException.of(ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<TagData> searchByName(String tagName) {
        try {
            return tagRepository.searchByName(tagName).stream()
                    .map(TagData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw ProductBusinessException.of(ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<ProductId> findLinkedProductIds(TagId tagId) {
        try {
            tagRepository.findById(tagId)
                    .orElseThrow(() -> ProductBusinessException.of(ProductErrors.TAG_NOT_FOUND_ERROR));
            return productTagRepository
                    .findAllByTagId(tagId)
                    .stream()
                    .map(ProductTag::getProductId)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            throw ProductBusinessException.of(ProductErrors.UNKNOWN_ERROR);
        }
    }

    public Tag findOrCreate(String tagName) {
        try {
            return tagRepository.findByName(tagName).orElseGet(() -> {
                try {
                    Tag newTag = Tag.create(idGenerator.generate(), tagName);
                    tagRepository.save(newTag);
                    return newTag;
                } catch (ProductException e) {
                    throw ProductBusinessException.of(ProductErrors.INVALID_TAG_ERROR);
                }
            });
        } catch (RepositoryException e) {
            throw ProductBusinessException.of(ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void remove(TagId tagId) {
        try {
            tagRepository.findById(tagId)
                    .orElseThrow(() -> ProductBusinessException.of(ProductErrors.TAG_NOT_FOUND_ERROR));
            productTagRepository.deleteAllByTagId(tagId);
            tagRepository.deleteById(tagId);
        } catch (RepositoryException e) {
            throw ProductBusinessException.of(ProductErrors.UNKNOWN_ERROR);
        }
    }
}
