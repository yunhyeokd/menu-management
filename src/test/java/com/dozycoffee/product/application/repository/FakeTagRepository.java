package com.dozycoffee.application.product.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeTagRepository implements TagRepository {

    private final Map<TagId, Tag> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public Tag put(Tag tag) {
        store.put(tag.getId(), tag);
        return tag;
    }

    public boolean contains(TagId tagId) {
        return store.containsKey(tagId);
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(Tag tag) throws RepositoryException {
        checkThrow();
        store.put(tag.getId(), tag);
    }

    @Override
    public List<Tag> findAll() throws RepositoryException {
        checkThrow();
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Tag> searchByName(String tagName) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(tag -> tag.getName().contains(tagName))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Tag> findById(TagId tagId) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(store.get(tagId));
    }

    @Override
    public Optional<Tag> findByName(String tagName) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(tag -> tag.getName().equals(tagName))
                .findFirst();
    }

    @Override
    public void deleteById(TagId tagId) throws RepositoryException {
        checkThrow();
        store.remove(tagId);
    }
}
