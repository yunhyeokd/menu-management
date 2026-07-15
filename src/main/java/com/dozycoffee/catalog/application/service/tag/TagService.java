package com.dozycoffee.catalog.application.service.tag;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.core.exception.service.ServiceCode;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.catalog.application.ProductServiceCode;
import com.dozycoffee.core.exception.service.ConflictException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.core.exception.service.ValidationException;
import com.dozycoffee.catalog.application.dto.TagData;
import com.dozycoffee.catalog.application.repository.TagRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.domain.ProductException;
import com.dozycoffee.catalog.domain.Tag;
import com.dozycoffee.catalog.domain.TagId;

import java.util.List;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final IdentifierGenerator<TagId> idGenerator;

    public TagService(TagRepository tagRepository, IdentifierGenerator<TagId> idGenerator) {
        this.tagRepository = tagRepository;
        this.idGenerator = idGenerator;
    }

    @Transactional(readOnly = true)
    public void assertExists(TagId tagId) {
        try {
            if (!tagRepository.existsById(tagId))
                throw new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.TAG_NOT_FOUND_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
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

    @Transactional(readOnly = true)
    public List<TagData> findAll() {
        try {
            return tagRepository.findAll().stream()
                    .map(TagData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<TagData> searchByName(String tagName) {
        try {
            return tagRepository.searchByName(tagName).stream()
                    .map(TagData::from)
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
            assertExists(tagId);
            tagRepository.deleteById(tagId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}
