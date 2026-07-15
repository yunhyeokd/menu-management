package com.dozycoffee.catalog.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class OptionItem {

    private static final int NAME_MAX_LENGTH = 30;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zㄱ-힣0-9 ]+$");
    private static final int DESC_MAX_LENGTH = 500;

    private final String name;
    private final String description;
    private final int price;
    private final Instant createdAt;

    private OptionItem(String name, String description, int price, Instant createdAt) {
        validateName(name);
        validateDescription(description);
        validatePrice(price);
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OptionItem optionItem)) return false;
        return Objects.equals(name, optionItem.name)
                && Objects.equals(description, optionItem.description)
                && price == optionItem.price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price);
    }

    public static OptionItem of(String name, String description, int price, Instant createdAt) {
        return new OptionItem(name, description, price, createdAt);
    }

    public static OptionItem create(String name, String description, int price) {
        return new OptionItem(name, description, price, Instant.now());
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

    private static void validatePrice(int price) {
        if (price < 0) throw new ProductException("invalid price");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
