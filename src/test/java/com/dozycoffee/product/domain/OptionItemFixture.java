package com.dozycoffee.product.domain;

import java.time.Instant;

public class OptionItemFixture {

    public static class Defaults {
        public static String name = "에스프레소 샷";
        public static String description = "음료에 추가할 에스프레소 샷 수량을 선택할 수 있습니다";
        public static int price = 500;
        public static Instant createdAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name = Defaults.name;
        private String description = Defaults.description;
        private int price = Defaults.price;
        private Instant createdAt = Defaults.createdAt;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OptionItem build() {
            return OptionItem.of(name, description, price, createdAt);
        }
    }
}
