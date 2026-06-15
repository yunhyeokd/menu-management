package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class Tag {
    private Long id;
    private String name;
    private Instant createdAt;

    private Tag(Long id, String name, Instant createdAt) {
        if (name == null) throw new ProductException("name is null");
        if (createdAt == null) throw new ProductException("createdAt is null");
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Tag of(long id, String name, Instant createdAt) {
        return new Tag(id, name, createdAt);
    }

    public static Tag create(String name) {
        validateName(name);
        return new Tag(null, name, Instant.now());
    }

    private static int NAME_MAX_LENGTH = 30;
    private static Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Zㄱ-힣0-9]+$");

    private static void validateName(String name) {
        if (name == null) {
            throw new ProductException("name is null");
        }
        if (name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new ProductException("invalid name length");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new ProductException("name does not match pattern");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag tag)) return false;
        if (id == null || tag.id==null) return false;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
