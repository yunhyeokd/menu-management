package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FakeTagRepository implements TagRepository {

    private final Map<Long, Tag> store = new LinkedHashMap<>();
    private long sequence = 1;
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public Tag put(Tag tag) {
        store.put(tag.getId(), tag);
        return tag;
    }

    public boolean contains(long tagId) {
        return store.containsKey(tagId);
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public Tag save(Tag tag) throws RepositoryException {
        checkThrow();
        if (tag.getId() == null) {
            Tag saved = Tag.of(sequence++, tag.getName(), tag.getCreatedAt());
            store.put(saved.getId(), saved);
            return saved;
        }
        store.put(tag.getId(), tag);
        return tag;
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
    public Tag findById(long tagId) throws RepositoryException {
        checkThrow();
        return store.get(tagId);
    }

    @Override
    public Tag findByName(String tagName) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(tag -> tag.getName().equals(tagName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(long tagId) throws RepositoryException {
        checkThrow();
        store.remove(tagId);
    }
}
