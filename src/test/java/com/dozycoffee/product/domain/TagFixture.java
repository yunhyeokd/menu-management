package com.dozycoffee.product.domain;

import java.time.Instant;

public class TagFixture {

    public static class Defaults {
        public static TagId id = TagId.of("00000000-0000-0000-0000-000000000001");
        public static String name = "신제품";
        public static Instant createdAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TagId id = Defaults.id;
        private String name = Defaults.name;
        private Instant createdAt = Defaults.createdAt;

        public Builder id(TagId id) {
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

        public Tag build() {
            return Tag.of(id, name, createdAt);
        }
    }
}
