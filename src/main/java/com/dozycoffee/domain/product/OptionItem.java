package com.dozycoffee.domain.product;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class OptionItem {

    private Long id;
    private long optionGroupId;
    private String name;
    private String description;
    private int price;
    private Instant createdAt;

    private OptionItem(Long id, long optionGroupId, String name, String description, int price, Instant createdAt) {
        if (name == null) throw new ProductException("name is null");
        if (createdAt == null) throw new ProductException("createdAt is null");
        if (price < 0) throw new ProductException("invalid price");
        this.id = id;
        this.optionGroupId = optionGroupId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdAt = createdAt;
    }

    public static OptionItem of(long id, long optionGroupId, String name, String description, int price, Instant createdAt) {
        return new OptionItem(id, optionGroupId, name, description, price, createdAt);
    }

    public static OptionItem create(long optionGroupId, String name, String description, int price) {
        validateName(name);
        validateDescription(description);
        return new OptionItem(null, optionGroupId, name, description, price, Instant.now());
    }

    private static int NAME_MAX_LENGTH = 30;
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

    private static void validatePrice(int price) {
        if (price < 0) {
            throw new ProductException("invalid price");
        }
    }

    public Long getId() {
        return id;
    }

    public long getOptionGroupId() {
        return optionGroupId;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OptionItem optionItem)) return false;
        if (id == null || optionItem.id==null) return false;
        return Objects.equals(id, optionItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
