package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
    void save(Tag tag) throws RepositoryException;

    boolean existsById(TagId tagId) throws RepositoryException;
    List<Tag> findAll() throws RepositoryException;
    List<Tag> searchByName(String tagName) throws RepositoryException;
    Optional<Tag> findById(TagId tagId) throws RepositoryException;
    Optional<Tag> findByName(String tagName) throws RepositoryException;
    List<Tag> findAllByNames(List<String> tagNames) throws RepositoryException;
    void deleteById(TagId tagId) throws RepositoryException;
}
