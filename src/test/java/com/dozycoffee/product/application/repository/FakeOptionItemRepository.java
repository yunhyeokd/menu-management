package com.dozycoffee.application.product.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.OptionItem;
import com.dozycoffee.product.domain.OptionItemId;

import java.util.*;
import java.util.stream.Collectors;

public class FakeOptionItemRepository implements OptionItemRepository {

    private final Map<OptionItemId, OptionItem> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public OptionItem put(OptionItem optionItem) {
        store.put(optionItem.getId(), optionItem);
        return optionItem;
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(OptionItem optionItem) throws RepositoryException {
        checkThrow();
        store.put(optionItem.getId(), optionItem);
    }

    @Override
    public Optional<OptionItem> findById(OptionItemId id) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<OptionItem> findAll() throws RepositoryException {
        checkThrow();
        return new ArrayList<>(store.values());
    }

    @Override
    public List<OptionItem> findAllByOptionGroupId(OptionGroupId optionGroupId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(item -> item.getOptionGroupId().equals(optionGroupId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByOptionGroupId(OptionGroupId optionGroupId) throws RepositoryException {
        checkThrow();
        store.values().removeIf(item -> item.getOptionGroupId().equals(optionGroupId));
    }
}
