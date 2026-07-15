package com.dozycoffee.catalog.domain;

import com.dozycoffee.branch.domain.BranchId;

import java.time.Instant;
import java.util.Set;

public class ProductFixture {

    public static class Defaults {
        public static ProductId id = ProductId.of("00000000-0000-0000-0000-000000000001");
        public static String name = "아메리카노";
        public static String description = "아메리카노입니다.";
        public static String imageUrl = "https://www.dozycoffee.com";
        public static CategoryId categoryId = CategoryId.of("00000000-0000-0000-0000-000000000001");
        public static int price = 1000;
        public static int kcal = 100;
        public static AllergenInfo allergenInfo = new AllergenInfo(Set.of(AllergenType.CASHEW));
        public static ProductKind kind = ProductKind.COMMON;
        public static BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        public static ProductStatus status = ProductStatus.ACTIVE;
        public static Instant createdAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ProductId id = Defaults.id;
        private String name = Defaults.name;
        private String description = Defaults.description;
        private String imageUrl = Defaults.imageUrl;
        private CategoryId categoryId = Defaults.categoryId;
        private int price = Defaults.price;
        private Integer kcal = Defaults.kcal;
        private BranchId branchId = Defaults.branchId;
        private AllergenInfo allergenInfo = Defaults.allergenInfo;
        private ProductKind kind = ProductKind.BRANCH_EXCLUSIVE;
        private ProductStatus status = Defaults.status;
        private Instant createdAt = Defaults.createdAt;

        public Builder id(ProductId id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder categoryId(CategoryId categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder kcal(int kcal) {
            this.kcal = kcal;
            return this;
        }

        public Builder allergenInfo(AllergenInfo allergenInfo) {
            this.allergenInfo = allergenInfo;
            return this;
        }

        public Builder branchId(BranchId branchId) {
            this.branchId = branchId;
            return this;
        }

        public Builder kind(ProductKind kind) {
            this.kind = kind;
            return this;
        }

        public Builder status(ProductStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Product createCommonProduct() {
            return Product.create(id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, ProductKind.COMMON, null);
        }

        public Product createBranchProduct() {
            return Product.create(id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, ProductKind.BRANCH_EXCLUSIVE, branchId);
        }

        public Product build() {
            return Product.of(
                    id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, kind, branchId, status, createdAt
            );
        }
    }

}
