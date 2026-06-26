package com.dozycoffee.product.domain;

import com.dozycoffee.branch.domain.BranchId;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class Product {

    private static final int NAME_MAX_LENGTH = 100;
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Zㄱ-힣0-9\\-() ]+");
    private static final int DESC_MAX_LENGTH = 1000;
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^https://[a-zA-Z0-9\\-.]+(?::[0-9]+)?(?:/[^\\s]*)?$");
    private static final int IMAGE_URL_MAX_LENGTH = 1000;

    private final ProductId id;
    private String name;
    private String description;
    private String imageUrl;
    private CategoryId categoryId;
    private int price;
    private Integer kcal;
    private AllergenInfo allergenInfo;
    private ProductKind kind;
    private BranchId branchId;
    private ProductStatus status;
    private final Instant createdAt;

    private Product(ProductId id, String name, String description, String imageUrl, CategoryId categoryId, int price, Integer kcal, AllergenInfo allergenInfo, ProductKind kind, BranchId branchId, ProductStatus status, Instant createdAt) {
        if (id == null) throw new ProductException("id cannot be null");
        if (createdAt == null) throw new ProductException("createdAt cannot be null");
        this.id = id;
        setName(name);
        setDescription(description);
        setImageUrl(imageUrl);
        setKind(kind);
        setPrice(price);
        setKcal(kcal);
        setStatus(status);
        setCategoryId(categoryId);
        setAllergenInfo(allergenInfo);
        setBranchId(branchId);
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static Product of(ProductId id, String name, String description, String imageUrl, CategoryId categoryId, int price, Integer kcal, AllergenInfo allergenInfo, ProductKind kind, BranchId branchId, ProductStatus status, Instant createdAt) {
        validateKindConflict(kind, branchId);
        validateNoCategoryStatus(categoryId, status);
        return new Product(id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, kind, branchId, status, createdAt);
    }

    public static Product createCommonProduct(ProductId id, String name, String description, String imageUrl, CategoryId categoryId, int price, Integer kcal, AllergenInfo allergenInfo) {
        return new Product(id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, ProductKind.COMMON, null, ProductStatus.INACTIVE, Instant.now());
    }

    public static Product createBranchProduct(ProductId id, String name, String description, String imageUrl, CategoryId categoryId, int price, Integer kcal, AllergenInfo allergenInfo, BranchId branchId) {
        return new Product(id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, ProductKind.BRANCH_EXCLUSIVE, branchId, ProductStatus.INACTIVE, Instant.now());
    }

    private static void validateKindConflict(ProductKind kind, BranchId branchId) {
        if (kind == ProductKind.COMMON && branchId != null) {
            throw new ProductException("common product must not have branch id");
        }
        if (kind == ProductKind.BRANCH_EXCLUSIVE && branchId == null) {
            throw new ProductException("branch exclusive product must have branch id");
        }
    }

    private static void validateNoCategoryStatus(CategoryId categoryId, ProductStatus productStatus) {
        if (categoryId == null && productStatus != ProductStatus.INACTIVE) {
            throw new ProductException("product without category must be inactive");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) throw new ProductException("Product name is null or blank");
        if (name.length() > NAME_MAX_LENGTH) throw new ProductException("Product name is too long");
        if (!NAME_PATTERN.matcher(name).matches() || !name.strip().equals(name)) throw new ProductException("Product name is invalid");
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > DESC_MAX_LENGTH) throw new ProductException("Product description is too long");
    }

    private static void validateImageUrl(String imageUrl) {
        if (imageUrl != null) {
            if (!IMAGE_URL_PATTERN.matcher(imageUrl).matches()) throw new ProductException("Product image url is invalid");
            if (imageUrl.length() > IMAGE_URL_MAX_LENGTH) throw new ProductException("Product image url is too long");
        }
    }

    private static void validatePrice(int price) {
        if (price < 0) throw new ProductException("Product price is negative");
    }

    private static void validateKcal(Integer kcal) {
        if (kcal != null && kcal < 0) throw new ProductException("Product kcal is negative");
    }

    public ProductId getId() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    private void setImageUrl(String imageUrl) {
        validateImageUrl(imageUrl);
        this.imageUrl = imageUrl;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    private void setCategoryId(CategoryId categoryId) {
        this.categoryId = categoryId;
    }

    public int getPrice() {
        return price;
    }

    private void setPrice(int price) {
        validatePrice(price);
        this.price = price;
    }

    public Integer getKcal() {
        return kcal;
    }

    private void setKcal(Integer kcal) {
        validateKcal(kcal);
        this.kcal = kcal;
    }

    public AllergenInfo getAllergenInfo() {
        return allergenInfo;
    }

    private void setAllergenInfo(AllergenInfo allergenInfo) {
        this.allergenInfo = allergenInfo;
    }

    public ProductKind getKind() {
        return kind;
    }

    private void setKind(ProductKind kind) {
        if (kind == null) throw new ProductException("Product kind is null");
        this.kind = kind;
    }

    public BranchId getBranchId() {
        return branchId;
    }

    private void setBranchId(BranchId branchId) {
        this.branchId = branchId;
    }

    public ProductStatus getStatus() {
        return status;
    }

    private void setStatus(ProductStatus status) {
        if (status == null) throw new ProductException("Product status is null");
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void activate() {
        if (categoryId == null) throw new ProductException("Category id is null");
        status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        status = ProductStatus.INACTIVE;
    }

    public void updatePrice(int price) {
        setPrice(price);
    }

    public void changeCategory(CategoryId categoryId) {
        this.categoryId = categoryId;
    }

    public void removeCategory() {
        deactivate();
        this.categoryId = null;
    }

    public void makeCommon() {
        this.branchId = null;
        this.kind = ProductKind.COMMON;
    }

    public void makeExclusive(BranchId branchId) {
        if (branchId == null) throw new ProductException("branchId cannot be null");
        this.branchId = branchId;
        this.kind = ProductKind.BRANCH_EXCLUSIVE;
    }

    public void updateName(String name) {
        setName(name);
    }

    public void updateDescription(String description) {
        setDescription(description);
    }

    public void updateImageUrl(String imageUrl) {
        setImageUrl(imageUrl);
    }

    public void updateKcal(Integer kcal) {
        setKcal(kcal);
    }

    public void updateAllergenInfo(AllergenInfo allergenInfo) {
        setAllergenInfo(allergenInfo);
    }
}
