package com.dozycoffee.product.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class Tag {

    private TagId id;
    private String name;
    private Instant createdAt;

    private Tag(TagId id, String name, Instant createdAt) {
        setId(id);
        setName(name);
        setCreatedAt(createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag tag)) return false;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static Tag of(TagId id, String name, Instant createdAt) {
        return new Tag(id, name, createdAt);
    }

    public static Tag create(TagId id, String name) {
        return new Tag(id, name, Instant.now());
    }

    public TagId getId() {
        return id;
    }

    private void setId(TagId id) {
        if (id == null) throw new ProductException("id cannot be null");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private static final int NAME_MAX_LENGTH = 30;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zㄱ-힣0-9]+$");

    private static void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new ProductException("invalid name length");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new ProductException("name does not match pattern");
        }
    }

    private void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
        this.createdAt = createdAt;
    }

    public void updateName(String newTagName) {
        setName(newTagName);
    }
}
