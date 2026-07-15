package com.dozycoffee.catalog.application.service.category;

import com.dozycoffee.core.exception.base.*;
import com.dozycoffee.core.exception.service.*;
import com.dozycoffee.catalog.application.dto.CategoryData;
import com.dozycoffee.catalog.application.repository.FakeCategoryRepository;
import com.dozycoffee.catalog.application.repository.FakeProductRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.domain.Category;
import com.dozycoffee.catalog.domain.CategoryFixture;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.ProductFixture;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CategoryServiceTest {

    private static final CategoryId NON_EXISTENT_ID = CategoryId.of("00000000-0000-0000-0000-000000000999");
    private static final CategoryId SECOND_ID = CategoryId.of("00000000-0000-0000-0000-000000000002");

    private FakeCategoryRepository categoryRepository;
    private FakeProductRepository productRepository;
    private CategoryService categoryService;
    private long nextCategoryId = 1L;

    @BeforeEach
    public void setUp() {
        categoryRepository = new FakeCategoryRepository();
        productRepository = new FakeProductRepository();
        nextCategoryId = 1L;
        categoryService = new CategoryService(categoryRepository, productRepository,
                () -> CategoryId.of(String.format("00000000-0000-0000-0000-%012d", nextCategoryId++)));
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((ServiceException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    // ─── create ──────────────────────────────────────────────────────────────

    @Test
    public void 카테고리를_정상_생성한다() {
        CategoryData created = categoryService.create("커피");

        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("커피");
        assertThat(categoryRepository.findById(created.id())).isPresent();
    }

    @Test
    public void 카테고리_생성시_이름이_중복되면_DUPLICATE_CATEGORY_NAME_ERROR를_던진다() {
        categoryRepository.put(CategoryFixture.builder().build());

        assertThatThrownBy(() -> categoryService.create("커피"))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.DUPLICATE_CATEGORY_NAME_ERROR));
    }

    @Test
    public void 카테고리_생성시_이름이_유효하지_않으면_INVALID_CATEGORY_ERROR를_던진다() {
        assertThatThrownBy(() -> categoryService.create(""))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.INVALID_CATEGORY_ERROR));
    }

    @Test
    public void 카테고리_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.create("커피"))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── updateName ──────────────────────────────────────────────────────────

    @Test
    public void 카테고리_이름을_정상_변경한다() {
        Category category = categoryRepository.put(CategoryFixture.builder().build());

        categoryService.updateName(category.getId(), "Coffee");

        assertThat(categoryRepository.findById(category.getId()).orElseThrow().getName()).isEqualTo("Coffee");
    }

    @Test
    public void 카테고리_이름_변경시_대상이_존재하지_않으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> categoryService.updateName(NON_EXISTENT_ID, "Coffee"))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    @Test
    public void 카테고리_이름_변경시_새_이름이_중복되면_DUPLICATE_CATEGORY_NAME_ERROR를_던진다() {
        categoryRepository.put(CategoryFixture.builder().build());
        categoryRepository.put(CategoryFixture.builder().id(SECOND_ID).name("음료").build());

        assertThatThrownBy(() -> categoryService.updateName(CategoryFixture.Defaults.id, "음료"))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.DUPLICATE_CATEGORY_NAME_ERROR));
    }

    @Test
    public void 카테고리_이름_변경시_새_이름이_유효하지_않으면_INVALID_CATEGORY_ERROR를_던진다() {
        categoryRepository.put(CategoryFixture.builder().build());

        assertThatThrownBy(() -> categoryService.updateName(CategoryFixture.Defaults.id, ""))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.INVALID_CATEGORY_ERROR));
    }

    @Test
    public void 카테고리_이름_변경중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.put(CategoryFixture.builder().build());
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.updateName(CategoryFixture.Defaults.id, "Coffee"))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── findAll ─────────────────────────────────────────────────────────────

    @Test
    public void 카테고리_전체_목록을_조회한다() {
        categoryRepository.put(CategoryFixture.builder().build());
        categoryRepository.put(CategoryFixture.builder().id(SECOND_ID).name("음료").build());

        List<CategoryData> categories = categoryService.findAll();

        assertThat(categories).hasSize(2);
    }

    @Test
    public void 카테고리가_없으면_빈_목록을_반환한다() {
        List<CategoryData> categories = categoryService.findAll();

        assertThat(categories).isEmpty();
    }

    @Test
    public void 카테고리_전체_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.findAll())
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── searchByName ────────────────────────────────────────────────────────

    @Test
    public void 이름으로_카테고리를_검색한다() {
        categoryRepository.put(CategoryFixture.builder().name("아이스커피").build());
        categoryRepository.put(CategoryFixture.builder().id(SECOND_ID).name("음료").build());

        List<CategoryData> result = categoryService.searchByName("커피");

        assertThat(result).extracting(CategoryData::name).containsExactly("아이스커피");
    }

    @Test
    public void 이름으로_검색시_일치하는_카테고리가_없으면_빈_목록을_반환한다() {
        categoryRepository.put(CategoryFixture.builder().build());

        List<CategoryData> result = categoryService.searchByName("존재하지않음");

        assertThat(result).isEmpty();
    }

    @Test
    public void 이름으로_카테고리_검색중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.searchByName("커피"))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── remove ──────────────────────────────────────────────────────────────

    @Test
    public void 카테고리를_정상_삭제한다() {
        Category category = categoryRepository.put(CategoryFixture.builder().build());

        categoryService.remove(category.getId());

        assertThat(categoryRepository.contains(category.getId())).isFalse();
    }

    @Test
    public void 카테고리_삭제시_연결된_상품이_비활성화된다() {
        Category category = categoryRepository.put(CategoryFixture.builder().build());
        productRepository.put(ProductFixture.builder().id(ProductFixture.Defaults.id).categoryId(CategoryFixture.Defaults.id).status(ProductStatus.ACTIVE).build());
        productRepository.put(ProductFixture.builder().id(ProductId.of("00000000-0000-0000-0000-000000000002")).categoryId(CategoryFixture.Defaults.id).status(ProductStatus.ACTIVE).build());

        categoryService.remove(category.getId());

        assertThat(productRepository.findAllByCategoryId(category.getId()))
                .allMatch(p -> p.getStatus() == ProductStatus.INACTIVE);
    }

    @Test
    public void 카테고리_삭제시_대상이_존재하지_않으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> categoryService.remove(NON_EXISTENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    @Test
    public void 카테고리_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.put(CategoryFixture.builder().build());
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.remove(CategoryFixture.Defaults.id))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }
}
