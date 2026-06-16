package com.dozycoffee.domain.product;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Product {

    Long id;
    String name;
    String description;
    String imageUrl;
    Long categoryId;
    int price;
    Integer kcal;
    AllergenInfo allergenInfo;
    ProductKind kind;
    Long branchId;
    ProductStatus status;
    Instant createdAt;

    private Product(Long id, String name, String description, String imageUrl, Long categoryId, int price, Integer kcal, AllergenInfo allergenInfo, ProductKind kind, Long branchId, ProductStatus status, Instant createdAt) {
        validateRequiredFields(name, kind, status, createdAt);
        validatePrice(price);
        validateKcal(kcal);
        validateCategoryStatus(categoryId, status);
        validateKindBranchConsistency(kind, branchId);
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.price = price;
        this.kcal = kcal;
        this.allergenInfo = allergenInfo;
        this.kind = kind;
        this.branchId = branchId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Product createCommonProduct(
            String name,
            String description,
            String imageUrl,
            long categoryId,
            int price,
            Integer kcal,
            AllergenInfo allergenInfo
    ) {
        validateName(name);
        validateDescription(description);
        validateImageUrl(imageUrl);

        return new Product(
                null,
                name,
                description,
                imageUrl,
                categoryId,
                price,
                kcal,
                allergenInfo,
                ProductKind.COMMON,
                null,
                ProductStatus.INACTIVE,
                Instant.now()
        );
    }

    public static Product createBranchProduct(
            String name,
            String description,
            String imageUrl,
            long categoryId,
            int price,
            Integer kcal,
            AllergenInfo allergenInfo,
            long branchId
    ) {
        validateName(name);
        validateDescription(description);
        validateImageUrl(imageUrl);

        return new Product(
                null,
                name,
                description,
                imageUrl,
                categoryId,
                price,
                kcal,
                allergenInfo,
                ProductKind.BRANCH_EXCLUSIVE,
                branchId,
                ProductStatus.INACTIVE,
                Instant.now()
        );
    }

    private static Pattern NAME_PATTERN = Pattern.compile("[a-zA-Zㄱ-힣0-9\\-() ]+");
    private static int NAME_MAX_LENGTH = 100;

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ProductException("Product name is null or blank");
        }
        if (name.length() > NAME_MAX_LENGTH) {
            throw new ProductException("Product name is too long");
        }
        if (!NAME_PATTERN.matcher(name).matches() || !name.strip().equals(name)) {
            throw new ProductException("Product name is invalid");
        }
    }

    private static int DESC_MAX_LENGTH = 1000;
    private static void validateDescription(String description) {
        if (description != null && description.length() > DESC_MAX_LENGTH) {
            throw new ProductException("Product description is too long");
        }
    }

    private static Pattern IMAGE_URL_PATTERN = Pattern.compile("^https://[a-zA-Z0-9\\-.]+(?::[0-9]+)?(?:/[^\\s]*)?$");
    private static int IMAGE_URL_MAX_LENGTH = 1000;

    private static void validateImageUrl(String imageUrl) {
        if (imageUrl != null) {
            if (!IMAGE_URL_PATTERN.matcher(imageUrl).matches()) {
                throw new ProductException("Product image url is invalid");
            }
            if (imageUrl.length() > IMAGE_URL_MAX_LENGTH) {
                throw new ProductException("Product image url is too long");
            }
        }
    }

    private static void validatePrice(int price) {
        if (price < 0) {
            throw new ProductException("Product price is negative");
        }
    }

    private static void validateKcal(Integer kcal) {
        if (kcal != null) {
            if (kcal < 0) {
                throw new ProductException("Product kcal is negative");
            }
        }
    }

    private static void validateRequiredFields(String name, ProductKind kind, ProductStatus status, Instant createdAt) {
        if (name == null) throw new ProductException("Product name is null");
        if (kind == null) throw new ProductException("Product kind is null");
        if (status == null) throw new ProductException("Product status is null");
        if (createdAt == null) throw new ProductException("Product createdAt is null");
    }

    private static void validateCategoryStatus(Long categoryId, ProductStatus status) {
        if (categoryId == null && status != ProductStatus.INACTIVE) {
            throw new ProductException("Product with no category must be inactive");
        }
    }

    private static void validateKindBranchConsistency(ProductKind kind, Long branchId) {
        if (kind == ProductKind.COMMON && branchId != null) {
            throw new ProductException("Common product must not have branchId");
        }
        if (kind == ProductKind.BRANCH_EXCLUSIVE && branchId == null) {
            throw new ProductException("Branch exclusive product must have branchId");
        }
    }

    public static Product of(long id, String name, String description, String imageUrl, Long categoryId, int price, Integer kcal, AllergenInfo allergenInfo, ProductKind kind, Long branchId, ProductStatus status, Instant createdAt) {
        return new Product(id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, kind, branchId, status, createdAt);
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

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public int getPrice() {
        return price;
    }

    public Integer getKcal() {
        return kcal;
    }

    public AllergenInfo getAllergenInfo() {
        return allergenInfo;
    }

    public ProductKind getKind() {
        return kind;
    }

    public Long getBranchId() {
        return branchId;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void activate() {
        if (categoryId == null) {
            throw new ProductException("Category id is null");
        }
        status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        status = ProductStatus.INACTIVE;
    }

    public void updatePrice(int price) {
        validatePrice(price);
        this.price = price;
    }

    public void changeCategory(long categoryId) {
        this.categoryId = categoryId;
    }

    public void removeCategory() {
        deactivate();
        this.categoryId = null;
    }

    public void makeCommon() {
        this.branchId = null;
        kind = ProductKind.COMMON;
    }

    public void makeExclusive(long branchId) {
        this.branchId = branchId;
        kind = ProductKind.BRANCH_EXCLUSIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product product)) return false;
        if (id == null || product.getId() == null) return false;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
