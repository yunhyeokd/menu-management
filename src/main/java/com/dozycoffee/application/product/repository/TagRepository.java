package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.Tag;

import java.util.List;

public interface TagRepository {

    Tag save(Tag tag) throws RepositoryException;

    List<Tag> findAll() throws RepositoryException;

    List<Tag> searchByName(String tagName) throws RepositoryException;

    Tag findById(long tagId) throws RepositoryException;

    Tag findByName(String tagName) throws RepositoryException;

    void deleteById(long tagId) throws RepositoryException;
}
