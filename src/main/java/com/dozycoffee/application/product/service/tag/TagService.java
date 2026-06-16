package com.dozycoffee.application.product.service.tag;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.repository.ProductTagRepository;
import com.dozycoffee.application.product.repository.TagRepository;
import com.dozycoffee.domain.product.ProductException;
import com.dozycoffee.domain.product.ProductTag;
import com.dozycoffee.domain.product.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class TagService {

    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;

    public TagService(
            TagRepository tagRepository,
            ProductTagRepository productTagRepository
    ) {
        this.tagRepository = tagRepository;
        this.productTagRepository = productTagRepository;
    }

    public Tag create(String tagName) {
        try {
            Tag tag = tagRepository.findByName(tagName);
            if (tag != null) {
                throw TagServiceException.with(TagErrors.DUPLICATE_NAME_ERROR);
            }
            try {
                Tag newTag = Tag.create(tagName);
                return tagRepository.save(newTag);
            } catch (ProductException e) {
                throw TagServiceException.with(TagErrors.INVALID_TAG_ERROR);
            }
        } catch (RepositoryException e) {
            throw TagServiceException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public void changeTagName(long tagId, String newTagName) {
        try {
            Tag newTag = tagRepository.findByName(newTagName);
            if (newTag != null) {
                throw TagServiceException.with(TagErrors.DUPLICATE_NAME_ERROR);
            }
            Tag tag = tagRepository.findById(tagId);
            if (tag == null) {
                throw TagServiceException.with(TagErrors.NOT_FOUND_ERROR);
            }
            try {
                tag.updateName(newTagName);
            } catch (ProductException e) {
                throw TagServiceException.with(TagErrors.INVALID_TAG_ERROR);
            }
            tagRepository.save(tag);
        } catch (RepositoryException e) {
            throw TagServiceException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public List<Tag> findAll() {
        try {
            return tagRepository.findAll();
        } catch (RepositoryException e) {
            throw TagServiceException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public List<Tag> searchByName(String tagName) {
        try {
            return tagRepository.searchByName(tagName);
        } catch (RepositoryException e) {
            throw TagServiceException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public List<Long> findLinkedProductIds(long tagId) {
        try {
            Tag tag = tagRepository.findById(tagId);
            if (tag == null) {
                throw TagServiceException.with(TagErrors.NOT_FOUND_ERROR);
            }
            return productTagRepository
                    .findAllByTagId(tagId)
                    .stream()
                    .map(ProductTag::getProductId)
                    .collect(Collectors.toList());
        } catch (RepositoryException e) {
            throw TagServiceException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

    public void remove(long tagId) {
        try {
            Tag tag = tagRepository.findById(tagId);
            if (tag == null) {
                throw TagServiceException.with(TagErrors.NOT_FOUND_ERROR);
            }
            productTagRepository.deleteAllByTagId(tagId);
            tagRepository.deleteById(tagId);
        } catch (RepositoryException e) {
            throw TagServiceException.with(TagErrors.UNKNOWN_ERROR);
        }
    }

}
