package com.dozycoffee.domain.product;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductOptionGroupTest {

    @Test
    public void 상품_옵션그룹을_정상_생성한다() {
        ProductOptionGroupId id = ProductOptionGroupId.of(1L);
        ProductId productId = ProductId.of(1L);
        OptionGroupId optionGroupId = OptionGroupId.of(1L);
        boolean isRequired = true;
        boolean allowMultiple = false;

        ProductOptionGroup productOptionGroup = ProductOptionGroup.create(id, productId, optionGroupId, isRequired, allowMultiple);

        assertThat(productId).isEqualTo(productOptionGroup.getProductId());
        assertThat(optionGroupId).isEqualTo(productOptionGroup.getOptionGroupId());
        assertThat(isRequired).isEqualTo(productOptionGroup.isRequired());
        assertThat(allowMultiple).isEqualTo(productOptionGroup.isAllowMultiple());
        assertThat(productOptionGroup.getCreatedAt()).isNotNull();
    }

    @Test
    public void id가_같은_상품_옵션그룹은_동등하다() {
        ProductOptionGroupId id = ProductOptionGroupId.of(1L);
        ProductId productId = ProductId.of(1L);
        OptionGroupId optionGroupId = OptionGroupId.of(1L);
        boolean isRequired = true;
        boolean allowMultiple = false;
        Instant createdAt = Instant.now();

        ProductOptionGroup productOptionGroup1 = ProductOptionGroup.of(id, productId, optionGroupId, isRequired, allowMultiple, createdAt);
        ProductOptionGroup productOptionGroup2 = ProductOptionGroup.of(id, productId, optionGroupId, isRequired, allowMultiple, createdAt);

        assertThat(productOptionGroup1.getId()).isEqualTo(productOptionGroup2.getId());
        assertThat(productOptionGroup1).isEqualTo(productOptionGroup2);
    }

    @Test
    public void id가_다른_상품_옵션그룹은_동등하지_않다() {
        ProductOptionGroupId id1 = ProductOptionGroupId.of(1L);
        ProductOptionGroupId id2 = ProductOptionGroupId.of(2L);

        ProductId productId = ProductId.of(1L);
        OptionGroupId optionGroupId = OptionGroupId.of(1L);
        boolean isRequired = true;
        boolean allowMultiple = false;
        Instant createdAt = Instant.now();

        ProductOptionGroup productOptionGroup1 = ProductOptionGroup.of(id1, productId, optionGroupId, isRequired, allowMultiple, createdAt);
        ProductOptionGroup productOptionGroup2 = ProductOptionGroup.of(id2, productId, optionGroupId, isRequired, allowMultiple, createdAt);

        assertThat(productOptionGroup1.getId()).isNotEqualTo(productOptionGroup2.getId());
        assertThat(productOptionGroup1).isNotEqualTo(productOptionGroup2);
    }

}
