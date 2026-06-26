package com.dozycoffee.product.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class OptionGroup {

    private static final int NAME_MAX_LENGTH = 50;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zㄱ-힣0-9 ]+$");
    private static final int DESC_MAX_LENGTH = 500;

    private final OptionGroupId id;
    private String name;
    private String description;
    private List<OptionItem> items;
    private Instant createdAt;

    private OptionGroup(OptionGroupId id, String name, String description, List<OptionItem> items, Instant createdAt) {
        if (id == null) throw new ProductException("id cannot be null");
        this.id = id;
        setName(name);
        setDescription(description);
        setItems(items);
        setCreatedAt(createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OptionGroup optionGroup)) return false;
        return Objects.equals(id, optionGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static OptionGroup of(OptionGroupId id, String name, String description, List<OptionItem> items, Instant createdAt) {
        return new OptionGroup(id, name, description, items, createdAt);
    }

    public static OptionGroup create(OptionGroupId id, String name, String description, List<OptionItem> items) {
        return new OptionGroup(id, name, description, items, Instant.now());
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new ProductException("invalid name length");
        }
        if (!NAME_PATTERN.matcher(name).matches() || !name.strip().equals(name)) {
            throw new ProductException("name does not match pattern");
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > DESC_MAX_LENGTH) {
            throw new ProductException("invalid description length");
        }
    }

    public OptionGroupId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    public List<OptionItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private void setItems(List<OptionItem> items) {
        if (items == null) throw new ProductException("items cannot be null");
        if (items.isEmpty()) throw new ProductException("items cannot be empty");
        this.items = new ArrayList<>(items);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
        this.createdAt = createdAt;
    }

    public void updateName(String name) {
        setName(name);
    }

    public void updateDescription(String description) {
        setDescription(description);
    }

    public void replaceItems(List<OptionItem> items) {
        setItems(items);
    }
}
