package com.dozycoffee.product.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductOptionGroupTest {

    @Test
    public void 상품_옵션그룹을_정상_생성한다() {
        ProductId productId = ProductFixture.Defaults.id;
        OptionGroupId optionGroupId = OptionGroupFixture.Defaults.id;
        boolean isRequired = true;
        boolean allowMultiple = false;

        ProductOptionGroup productOptionGroup = ProductOptionGroup.create(productId, optionGroupId, isRequired, allowMultiple);

        assertThat(productId).isEqualTo(productOptionGroup.getProductId());
        assertThat(optionGroupId).isEqualTo(productOptionGroup.getOptionGroupId());
        assertThat(isRequired).isEqualTo(productOptionGroup.isRequired());
        assertThat(allowMultiple).isEqualTo(productOptionGroup.isAllowMultiple());
        assertThat(productOptionGroup.getCreatedAt()).isNotNull();
    }

    @Test
    public void 복합키가_같은_상품_옵션그룹은_동등하다() {
        ProductId productId = ProductFixture.Defaults.id;
        OptionGroupId optionGroupId = OptionGroupFixture.Defaults.id;
        Instant createdAt = Instant.now();

        ProductOptionGroup productOptionGroup1 = ProductOptionGroup.of(productId, optionGroupId, true, false, createdAt);
        ProductOptionGroup productOptionGroup2 = ProductOptionGroup.of(productId, optionGroupId, true, false, createdAt);

        assertThat(productOptionGroup1).isEqualTo(productOptionGroup2);
    }

    @Test
    public void optionGroupId가_다른_상품_옵션그룹은_동등하지_않다() {
        ProductId productId = ProductFixture.Defaults.id;
        Instant createdAt = Instant.now();

        ProductOptionGroup productOptionGroup1 = ProductOptionGroup.of(productId, OptionGroupFixture.Defaults.id, true, false, createdAt);
        ProductOptionGroup productOptionGroup2 = ProductOptionGroup.of(productId, OptionGroupId.of("00000000-0000-0000-0000-000000000002"), true, false, createdAt);

        assertThat(productOptionGroup1).isNotEqualTo(productOptionGroup2);
    }

}
