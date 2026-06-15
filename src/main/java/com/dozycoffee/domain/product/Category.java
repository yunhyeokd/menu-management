package com.dozycoffee.domain.product;

import com.dozycoffee.domain.common.DomainCode;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class Category {
    private Long id;
    private String name;
    private Instant createdAt;

    private Category(Long id, String name, Instant createdAt) {
        if (name == null) throw new ProductException("name is null");
        if (createdAt == null) throw new ProductException("createdAt is null");
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Category of(long id, String name, Instant createdAt) {
        return new Category(id, name, createdAt);
    }

    public static Category create(String name) {
        validateName(name);
        return new Category(null, name, Instant.now());
    }

    private static int NAME_MAX_LENGTH = 50;
    private static Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Zㄱ-힣0-9 ]+$");

    private static void validateName(String name) {
        if (name == null) {
            throw new ProductException("name is null");
        }
        if (name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new ProductException("invalid name length");
        }
        if (
                !NAME_PATTERN.matcher(name).matches()
                || !name.strip().equals(name)
        ) {
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
        if (!(o instanceof Category category)) return false;
        if (id == null || category.id==null) return false;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
