package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.TagRow;
import com.dozycoffee.infrastructure.persistance.mapper.TagMapper;
import com.dozycoffee.product.application.repository.TagRepository;
import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisTagRepository implements TagRepository {

    private final TagMapper tagMapper;

    @Override
    public void save(Tag tag) throws RepositoryException {
        tagMapper.upsert(toRow(tag));
    }

    @Override
    public boolean existsById(TagId tagId) throws RepositoryException {
        return tagMapper.existsById(tagId.getValue());
    }

    @Override
    public List<Tag> findAll() throws RepositoryException {
        return tagMapper.findAll().stream().map(TagRow::toTag).toList();
    }

    @Override
    public List<Tag> searchByName(String tagName) throws RepositoryException {
        return tagMapper.searchByName(tagName).stream().map(TagRow::toTag).toList();
    }

    @Override
    public Optional<Tag> findById(TagId tagId) throws RepositoryException {
        return tagMapper.findById(tagId.getValue()).map(TagRow::toTag);
    }

    @Override
    public Optional<Tag> findByName(String tagName) throws RepositoryException {
        return tagMapper.findByName(tagName).map(TagRow::toTag);
    }

    @Override
    public List<Tag> findAllByNames(List<String> tagNames) throws RepositoryException {
        if (tagNames.isEmpty()) return List.of();
        return tagMapper.findAllByNames(tagNames).stream().map(TagRow::toTag).toList();
    }

    @Override
    public void deleteById(TagId tagId) throws RepositoryException {
        tagMapper.deleteById(tagId.getValue());
    }

    private static TagRow toRow(Tag tag) {
        return new TagRow(tag.getId().getValue(), tag.getName(), tag.getCreatedAt());
    }
}
