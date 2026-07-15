package com.dozycoffee.catalog.domain;

import java.time.Instant;
import java.util.List;

public class OptionGroupFixture {

    public static class Defaults {
        public static OptionGroupId id = OptionGroupId.of("00000000-0000-0000-0000-000000000001");
        public static String name = "샷 옵션";
        public static String description = "음료에 추가할 샷 옵션을 선택할 수 있습니다";
        public static List<OptionItem> items = List.of(OptionItemFixture.builder().build());
        public static Instant createdAt = Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OptionGroupId id = Defaults.id;
        private String name = Defaults.name;
        private String description = Defaults.description;
        private List<OptionItem> items = Defaults.items;
        private Instant createdAt = Defaults.createdAt;

        public Builder id(OptionGroupId id) {
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

        public Builder items(List<OptionItem> items) {
            this.items = items;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OptionGroup build() {
            return OptionGroup.of(id, name, description, items, createdAt);
        }
    }
}
