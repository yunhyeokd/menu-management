package com.dozycoffee.application.product.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.domain.OptionGroup;
import com.dozycoffee.product.domain.OptionGroupId;

import java.util.*;

public class FakeOptionGroupRepository implements OptionGroupRepository {

    private final Map<OptionGroupId, OptionGroup> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public OptionGroup put(OptionGroup optionGroup) {
        store.put(optionGroup.getId(), optionGroup);
        return optionGroup;
    }

    public boolean contains(OptionGroupId id) {
        return store.containsKey(id);
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(OptionGroup optionGroup) throws RepositoryException {
        checkThrow();
        store.put(optionGroup.getId(), optionGroup);
    }

    @Override
    public Optional<OptionGroup> findById(OptionGroupId id) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<OptionGroup> findAll() throws RepositoryException {
        checkThrow();
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(OptionGroupId id) throws RepositoryException {
        checkThrow();
        store.remove(id);
    }
}
