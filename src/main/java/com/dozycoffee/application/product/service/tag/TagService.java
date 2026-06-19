package com.dozycoffee.application.product.service.tag;

import com.dozycoffee.application.common.IdentifierGenerator;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.repository.ProductTagRepository;
import com.dozycoffee.application.product.repository.TagRepository;
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

    public Tag create(String tagName) {
        try {
            if (tagRepository.findByName(tagName).isPresent()) {
                throw TagBusinessException.with(TagErrors.DUPLICATE_NAME_ERROR);
            }
            try {
                Tag newTag = Tag.create(idGenerator.generate(), tagName);
                tagRepository.save(newTag);
                return newTag;
            } catch (ProductException e) {
                throw TagBusinessException.with(TagErrors.INVALID_TAG_ERROR);
            }
        } catch (RepositoryException e) {
            throw TagBusinessException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public void changeTagName(TagId tagId, String newTagName) {
        try {
            if (tagRepository.findByName(newTagName).isPresent()) {
                throw TagBusinessException.with(TagErrors.DUPLICATE_NAME_ERROR);
            }
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> TagBusinessException.with(TagErrors.NOT_FOUND_ERROR));
            try {
                tag.updateName(newTagName);
            } catch (ProductException e) {
                throw TagBusinessException.with(TagErrors.INVALID_TAG_ERROR);
            }
            tagRepository.save(tag);
        } catch (RepositoryException e) {
            throw TagBusinessException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public List<Tag> findAll() {
        try {
            return tagRepository.findAll();
        } catch (RepositoryException e) {
            throw TagBusinessException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public List<Tag> searchByName(String tagName) {
        try {
            return tagRepository.searchByName(tagName);
        } catch (RepositoryException e) {
            throw TagBusinessException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public List<ProductId> findLinkedProductIds(TagId tagId) {
        try {
            tagRepository.findById(tagId)
                    .orElseThrow(() -> TagBusinessException.with(TagErrors.NOT_FOUND_ERROR));
            return productTagRepository
                    .findAllByTagId(tagId)
                    .stream()
                    .map(ProductTag::getProductId)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            throw TagBusinessException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public void remove(TagId tagId) {
        try {
            tagRepository.findById(tagId)
                    .orElseThrow(() -> TagBusinessException.with(TagErrors.NOT_FOUND_ERROR));
            productTagRepository.deleteAllByTagId(tagId);
            tagRepository.deleteById(tagId);
        } catch (RepositoryException e) {
            throw TagBusinessException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

}
