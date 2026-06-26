package com.dozycoffee.product.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class OptionGroup {

    private OptionGroupId id;
    private String name;
    private String description;
    private Instant createdAt;

    private OptionGroup(OptionGroupId id, String name, String description, Instant createdAt) {
        setId(id);
        setName(name);
        setDescription(description);
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

    public static OptionGroup of(OptionGroupId id, String name, String description, Instant createdAt) {
        return new OptionGroup(id, name, description, createdAt);
    }

    public static OptionGroup create(OptionGroupId id, String name, String description) {
        return new OptionGroup(id, name, description, Instant.now());
    }

    public OptionGroupId getId() {
        return id;
    }

    private void setId(OptionGroupId id) {
        if (id == null) throw new ProductException("id cannot be null");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private static final int NAME_MAX_LENGTH = 50;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zㄱ-힣0-9 ]+$");

    private static void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new ProductException("invalid name length");
        }
        if (!NAME_PATTERN.matcher(name).matches() || !name.strip().equals(name)) {
            throw new ProductException("name does not match pattern");
        }
    }

    private void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private static final int DESC_MAX_LENGTH = 500;

    private static void validateDescription(String description) {
        if (description != null && description.length() > DESC_MAX_LENGTH) {
            throw new ProductException("invalid description length");
        }
    }

    private void setDescription(String description) {
        validateDescription(description);
        this.description = description;
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
}
