package com.dozycoffee.product.domain;

import com.dozycoffee.branch.domain.BranchId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductTest {

    @Test
    public void 공통_상품을_정상_생성한다() {
        Product product = ProductFixture.builder()
                .name(ProductFixture.Base.name)
                .description(ProductFixture.Base.description)
                .imageUrl(ProductFixture.Base.imageUrl)
                .categoryId(ProductFixture.Base.categoryId)
                .price(ProductFixture.Base.price)
                .kcal(ProductFixture.Base.kcal)
                .allergenInfo(ProductFixture.Base.allergenInfo)
                .createCommonProduct();

        assertThat(product.getName()).isEqualTo(ProductFixture.Base.name);
        assertThat(product.getDescription()).isEqualTo(ProductFixture.Base.description);
        assertThat(product.getImageUrl()).isEqualTo(ProductFixture.Base.imageUrl);
        assertThat(product.getCategoryId()).isEqualTo(ProductFixture.Base.categoryId);
        assertThat(product.getPrice()).isEqualTo(ProductFixture.Base.price);
        assertThat(product.getKcal()).isEqualTo(ProductFixture.Base.kcal);
        assertThat(product.getAllergenInfo()).isEqualTo(ProductFixture.Base.allergenInfo);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    public void 지점_전용_상품을_정상_생성한다() {
        Product product = ProductFixture.builder()
                .name(ProductFixture.Base.name)
                .description(ProductFixture.Base.description)
                .imageUrl(ProductFixture.Base.imageUrl)
                .categoryId(ProductFixture.Base.categoryId)
                .price(ProductFixture.Base.price)
                .kcal(ProductFixture.Base.kcal)
                .allergenInfo(ProductFixture.Base.allergenInfo)
                .branchId(ProductFixture.Base.branchId)
                .createBranchProduct();

        assertThat(product.getName()).isEqualTo(ProductFixture.Base.name);
        assertThat(product.getDescription()).isEqualTo(ProductFixture.Base.description);
        assertThat(product.getImageUrl()).isEqualTo(ProductFixture.Base.imageUrl);
        assertThat(product.getCategoryId()).isEqualTo(ProductFixture.Base.categoryId);
        assertThat(product.getPrice()).isEqualTo(ProductFixture.Base.price);
        assertThat(product.getKcal()).isEqualTo(ProductFixture.Base.kcal);
        assertThat(product.getAllergenInfo()).isEqualTo(ProductFixture.Base.allergenInfo);
        assertThat(product.getBranchId()).isEqualTo(ProductFixture.Base.branchId);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "#product", " product "
    })
    public void 상품_생성시_상품명이_유효하지_않으면_예외가_발생한다(String invalidName) {
        ProductFixture.Builder builder = ProductFixture.builder().name(invalidName);

        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_생성시_상품명이_최대_길이보다_크면_예외가_발생한다() {
        ProductFixture.Builder builder = ProductFixture.builder().name("a".repeat(101));

        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_생성시_상품설명이_최대_길이보다_크면_예외가_발생한다() {
        ProductFixture.Builder builder = ProductFixture.builder().description("a".repeat(1001));
        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", " ",
            "http://example.com", " https://example.com", "abchttps://example.com",
    })
    public void 상품_생성시_상품이미지_URL이_유효하지_않으면_예외가_발생한다(String invalidUrl) {
        ProductFixture.Builder builder = ProductFixture.builder().imageUrl(invalidUrl);

        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_생성시_상품이미지_URL이_최대_길이보다_크면_예외가_발생한다() {
        ProductFixture.Builder builder = ProductFixture.builder().imageUrl("https://example.com/" + "a".repeat(981));
        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_생성시_상품_가격이_0보다_작으면_예외가_발생한다() {
        ProductFixture.Builder builder = ProductFixture.builder().price(-1);
        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void id가_같은_상품은_동등하다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product1 = builder.id(ProductId.of("00000000-0000-0000-0000-000000000001")).build();
        Product product2 = builder.id(ProductId.of("00000000-0000-0000-0000-000000000001")).build();
        assertThat(product1).isEqualTo(product2);
    }

    @Test
    public void id가_다른_상품은_동등하지_않다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product1 = builder.id(ProductId.of("00000000-0000-0000-0000-000000000001")).build();
        Product product2 = builder.id(ProductId.of("00000000-0000-0000-0000-000000000002")).build();
        assertThat(product1).isNotEqualTo(product2);
    }



    @Test
    public void id가_null인_상품은_동등하지_않다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product1 = builder.id(ProductId.of("00000000-0000-0000-0000-000000000001")).build();
        Product product2 = builder.id(ProductId.of("00000000-0000-0000-0000-000000000002")).createCommonProduct();
        assertThat(product1).isNotEqualTo(product2);
    }


    @Test
    public void 카테고리를_제거한_상품은_비활성화된다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product = builder.createCommonProduct();
        product.removeCategory();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.getCategoryId()).isNull();
    }

    @Test
    public void 카테고리가_없는_상품은_활성화할_수_없다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product = builder.status(ProductStatus.INACTIVE).categoryId(null).build();
        assertThatThrownBy(product::activate).isInstanceOf(ProductException.class);
    }

    @Test
    public void 공통_상품에_branchId가_있으면_예외가_발생한다() {
        assertThatThrownBy(() -> ProductFixture.builder()
                .kind(ProductKind.COMMON)
                .branchId(BranchId.of("00000000-0000-0000-0000-000000000001"))
                .build()
        ).isInstanceOf(ProductException.class);
    }

    @Test
    public void 지점_전용_상품에_branchId가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> ProductFixture.builder()
                .kind(ProductKind.BRANCH_EXCLUSIVE)
                .branchId(null)
                .build()
        ).isInstanceOf(ProductException.class);
    }

    @Test
    public void categoryId가_없는_상품이_활성_상태이면_예외가_발생한다() {
        assertThatThrownBy(() -> ProductFixture.builder()
                .categoryId(null)
                .status(ProductStatus.ACTIVE)
                .build()
        ).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_가격을_0보다_작게_변경할_수_없다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product = builder.createCommonProduct();
        assertThatThrownBy(() -> product.updatePrice(-1))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 활성_상품을_비활성화하면_상태가_INACTIVE로_바뀐다() {
        Product product = ProductFixture.builder().status(ProductStatus.ACTIVE).build();

        product.deactivate();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    public void 상품의_카테고리를_변경한다() {
        Product product = ProductFixture.builder().createCommonProduct();
        CategoryId newCategoryId = CategoryId.of("00000000-0000-0000-0000-000000000002");

        product.changeCategory(newCategoryId);

        assertThat(product.getCategoryId()).isEqualTo(newCategoryId);
    }

    @Test
    public void 상품명을_변경한다() {
        Product product = ProductFixture.builder().createCommonProduct();

        product.updateName("라떼");

        assertThat(product.getName()).isEqualTo("라떼");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "#product"})
    public void 상품명_변경시_유효하지_않으면_예외가_발생한다(String invalidName) {
        Product product = ProductFixture.builder().createCommonProduct();

        assertThatThrownBy(() -> product.updateName(invalidName))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_설명을_변경한다() {
        Product product = ProductFixture.builder().createCommonProduct();

        product.updateDescription("변경된 설명");

        assertThat(product.getDescription()).isEqualTo("변경된 설명");
    }

    @Test
    public void 상품_설명_변경시_최대_길이보다_크면_예외가_발생한다() {
        Product product = ProductFixture.builder().createCommonProduct();

        assertThatThrownBy(() -> product.updateDescription("a".repeat(1001)))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_이미지_URL을_변경한다() {
        Product product = ProductFixture.builder().createCommonProduct();

        product.updateImageUrl("https://example.com/new.png");

        assertThat(product.getImageUrl()).isEqualTo("https://example.com/new.png");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "http://example.com"})
    public void 상품_이미지_URL_변경시_유효하지_않으면_예외가_발생한다(String invalidUrl) {
        Product product = ProductFixture.builder().createCommonProduct();

        assertThatThrownBy(() -> product.updateImageUrl(invalidUrl))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_칼로리를_변경한다() {
        Product product = ProductFixture.builder().createCommonProduct();

        product.updateKcal(200);

        assertThat(product.getKcal()).isEqualTo(200);
    }

    @Test
    public void 상품_칼로리를_0보다_작게_변경할_수_없다() {
        Product product = ProductFixture.builder().createCommonProduct();

        assertThatThrownBy(() -> product.updateKcal(-1))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품의_알러젠_정보를_변경한다() {
        Product product = ProductFixture.builder().createCommonProduct();
        AllergenInfo newAllergenInfo = new AllergenInfo(Set.of(AllergenType.MILK));

        product.updateAllergenInfo(newAllergenInfo);

        assertThat(product.getAllergenInfo()).isEqualTo(newAllergenInfo);
    }

    @Test
    public void 지점_전용_상품을_공통_상품으로_전환한다() {
        Product product = ProductFixture.builder().createBranchProduct();

        product.makeCommon();

        assertThat(product.getKind()).isEqualTo(ProductKind.COMMON);
        assertThat(product.getBranchId()).isNull();
    }

    @Test
    public void 공통_상품을_지점_전용_상품으로_전환한다() {
        Product product = ProductFixture.builder().createCommonProduct();
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000002");

        product.makeExclusive(branchId);

        assertThat(product.getKind()).isEqualTo(ProductKind.BRANCH_EXCLUSIVE);
        assertThat(product.getBranchId()).isEqualTo(branchId);
    }

    @Test
    public void 지점_전용_상품으로_전환시_branchId가_없으면_예외가_발생한다() {
        Product product = ProductFixture.builder().createCommonProduct();

        assertThatThrownBy(() -> product.makeExclusive(null))
                .isInstanceOf(ProductException.class);
    }

}
