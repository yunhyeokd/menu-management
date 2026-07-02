package com.dozycoffee.branch.application.model;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.ProductId;

import java.util.List;
import java.util.Objects;

public class BranchProduct {

    private final ProductId productId;
    private final BranchId branchId;
    private final boolean active;
    private final String name;
    private final int price;
    private final String imageUrl;
    private final List<String> tags;
    private final CategoryId categoryId;

    private BranchProduct(ProductId productId, BranchId branchId, boolean active, String name, int price, String imageUrl, List<String> tags, CategoryId categoryId) {
        this.productId = productId;
        this.branchId = branchId;
        this.active = active;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.categoryId = categoryId;
    }

    public static BranchProduct of(ProductId productId, BranchId branchId, boolean active, String name, int price, String imageUrl, List<String> tags, CategoryId categoryId) {
        return new BranchProduct(productId, branchId, active, name, price, imageUrl, tags, categoryId);
    }

    public static BranchProduct of(ProductId productId, BranchId branchId, boolean active) {
        return new BranchProduct(productId, branchId, active, null, 0, null, List.of(), null);
    }

    public ProductId getProductId() {
        return productId;
    }

    public BranchId getBranchId() {
        return branchId;
    }

    public boolean isActive() {
        return active;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public boolean isCommon() {
        return branchId == null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BranchProduct that)) return false;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId);
    }
}
