package com.dozycoffee.domain.product;

import com.dozycoffee.domain.branch.BranchId;

import java.time.Instant;
import java.util.Set;

public class ProductFixture {

    public static class Base {
        public static ProductId id = ProductId.of(1L);
        public static String name = "아메리카노";
        public static String description = "아메리카노입니다.";
        public static String imageUrl = "https://www.dozycoffee.com";
        public static CategoryId categoryId = CategoryId.of(1L);
        public static int price = 1000;
        public static int kcal = 100;
        public static AllergenInfo allergenInfo = new AllergenInfo(Set.of(AllergenType.CASHEW));
        public static ProductKind kind = ProductKind.COMMON;
        public static BranchId branchId = BranchId.of(1L);
        public static ProductStatus status = ProductStatus.ACTIVE;
        public static Instant createdAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ProductId id = Base.id;
        private String name = Base.name;
        private String description = Base.description;
        private String imageUrl = Base.imageUrl;
        private CategoryId categoryId = Base.categoryId;
        private int price = Base.price;
        private Integer kcal = Base.kcal;
        private BranchId branchId = Base.branchId;
        private AllergenInfo allergenInfo = Base.allergenInfo;
        private ProductKind kind = ProductKind.BRANCH_EXCLUSIVE;
        private ProductStatus status = Base.status;
        private Instant createdAt = Base.createdAt;

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
            return Product.createCommonProduct(id, name, description, imageUrl, categoryId, price, kcal, allergenInfo);
        }

        public Product createBranchProduct() {
            return Product.createBranchProduct(id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, branchId);
        }

        public Product build() {
            return Product.of(
                    id, name, description, imageUrl, categoryId, price, kcal, allergenInfo, kind, branchId, status, createdAt
            );
        }
    }

}
