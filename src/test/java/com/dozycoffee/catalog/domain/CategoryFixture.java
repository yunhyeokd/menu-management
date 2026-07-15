package com.dozycoffee.catalog.domain;

import java.time.Instant;

public class CategoryFixture {

    public static class Defaults {
        public static CategoryId id = CategoryId.of("00000000-0000-0000-0000-000000000001");
        public static String name = "커피";
        public static Instant createdAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CategoryId id = Defaults.id;
        private String name = Defaults.name;
        private Instant createdAt = Defaults.createdAt;

        public Builder id(CategoryId id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Category build() {
            return Category.of(id, name, createdAt);
        }
    }
}
