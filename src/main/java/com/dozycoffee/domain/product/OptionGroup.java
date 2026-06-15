package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class OptionGroup {

    private Long id;
    private String name;
    private String description;
    private Instant createdAt;

    private OptionGroup(Long id, String name, String description, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static OptionGroup of(long id, String name, String description, Instant createdAt) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(createdAt);
        return new OptionGroup(id, name, description, createdAt);
    }

    public static OptionGroup create(String name, String description) {
        validateName(name);
        validateDescription(description);
        return new OptionGroup(null, name, description, Instant.now());
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

    private static int DESC_MAX_LENGTH = 500;

    private static void validateDescription(String description) {
        if (description != null && description.length() > DESC_MAX_LENGTH) {
            throw new ProductException("invalid description length");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OptionGroup optionGroup)) return false;
        if (id == null || optionGroup.id==null) return false;
        return Objects.equals(id, optionGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
