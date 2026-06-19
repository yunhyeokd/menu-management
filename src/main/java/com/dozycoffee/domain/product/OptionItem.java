package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class OptionItem {

    private OptionItemId id;
    private OptionGroupId optionGroupId;
    private String name;
    private String description;
    private int price;
    private Instant createdAt;

    private OptionItem(OptionItemId id, OptionGroupId optionGroupId, String name, String description, int price, Instant createdAt) {
        setId(id);
        setOptionGroupId(optionGroupId);
        setName(name);
        setDescription(description);
        setPrice(price);
        setCreatedAt(createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OptionItem optionItem)) return false;
        return Objects.equals(id, optionItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static OptionItem of(OptionItemId id, OptionGroupId optionGroupId, String name, String description, int price, Instant createdAt) {
        return new OptionItem(id, optionGroupId, name, description, price, createdAt);
    }

    public static OptionItem create(OptionItemId id, OptionGroupId optionGroupId, String name, String description, int price) {
        return new OptionItem(id, optionGroupId, name, description, price, Instant.now());
    }

    public OptionItemId getId() {
        return id;
    }

    private void setId(OptionItemId id) {
        if (id == null) throw new ProductException("id cannot be null");
        this.id = id;
    }

    public OptionGroupId getOptionGroupId() {
        return optionGroupId;
    }

    private void setOptionGroupId(OptionGroupId optionGroupId) {
        if (optionGroupId == null) throw new ProductException("optionGroupId cannot be null");
        this.optionGroupId = optionGroupId;
    }

    public String getName() {
        return name;
    }

    private static final int NAME_MAX_LENGTH = 30;
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

    public int getPrice() {
        return price;
    }

    private static void validatePrice(int price) {
        if (price < 0) throw new ProductException("invalid price");
    }

    private void setPrice(int price) {
        validatePrice(price);
        this.price = price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
        this.createdAt = createdAt;
    }
}
