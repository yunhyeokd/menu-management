package com.dozycoffee.product.application.service.tag;

import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.core.application.exception.ConflictException;
import com.dozycoffee.core.application.exception.ResourceNotFoundException;
import com.dozycoffee.core.application.exception.SystemException;
import com.dozycoffee.core.application.exception.ValidationException;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.repository.ProductTagRepository;
import com.dozycoffee.product.application.repository.TagRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductException;
import com.dozycoffee.product.domain.ProductTag;
import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;

import java.util.List;

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
                throw new ConflictException(ProductServiceCode.PRD, ProductErrors.DUPLICATE_TAG_NAME_ERROR);
            }
            try {
                Tag newTag = Tag.create(idGenerator.generate(), tagName);
                tagRepository.save(newTag);
                return TagData.from(newTag);
            } catch (ProductException e) {
                throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_TAG_ERROR);
            }
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void changeTagName(TagId tagId, String newTagName) {
        try {
            if (tagRepository.findByName(newTagName).isPresent()) {
                throw new ConflictException(ProductServiceCode.PRD, ProductErrors.DUPLICATE_TAG_NAME_ERROR);
            }
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.TAG_NOT_FOUND_ERROR));
            try {
                tag.updateName(newTagName);
            } catch (ProductException e) {
                throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_TAG_ERROR);
            }
            tagRepository.save(tag);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<TagData> findAll() {
        try {
            return tagRepository.findAll().stream()
                    .map(TagData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<TagData> searchByName(String tagName) {
        try {
            return tagRepository.searchByName(tagName).stream()
                    .map(TagData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<ProductId> findLinkedProductIds(TagId tagId) {
        try {
            tagRepository.findById(tagId)
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.TAG_NOT_FOUND_ERROR));
            return productTagRepository
                    .findAllByTagId(tagId)
                    .stream()
                    .map(ProductTag::getProductId)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
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
                    throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_TAG_ERROR);
                }
            });
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void remove(TagId tagId) {
        try {
            tagRepository.findById(tagId)
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.TAG_NOT_FOUND_ERROR));
            productTagRepository.deleteAllByTagId(tagId);
            tagRepository.deleteById(tagId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}
