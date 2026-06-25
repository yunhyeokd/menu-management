package com.dozycoffee.application.product.service.category;

import com.dozycoffee.application.common.AppException;
import com.dozycoffee.application.common.ServiceError;
import com.dozycoffee.application.common.exception.*;
import com.dozycoffee.application.product.dto.CategoryData;
import com.dozycoffee.application.product.repository.FakeCategoryRepository;
import com.dozycoffee.application.product.repository.FakeProductRepository;
import com.dozycoffee.application.product.service.ProductErrors;
import com.dozycoffee.domain.product.Category;
import com.dozycoffee.domain.product.CategoryId;
import com.dozycoffee.domain.product.ProductFixture;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CategoryServiceTest {

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
                () -> CategoryId.of(nextCategoryId++));
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
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
        categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));

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
        Category category = categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));

        categoryService.updateName(category.getId(), "Coffee");

        assertThat(categoryRepository.findById(category.getId()).orElseThrow().getName()).isEqualTo("Coffee");
    }

    @Test
    public void 카테고리_이름_변경시_대상이_존재하지_않으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> categoryService.updateName(CategoryId.of(999L), "Coffee"))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    @Test
    public void 카테고리_이름_변경시_새_이름이_중복되면_DUPLICATE_CATEGORY_NAME_ERROR를_던진다() {
        categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));
        categoryRepository.put(Category.of(CategoryId.of(2L), "음료", Instant.now()));

        assertThatThrownBy(() -> categoryService.updateName(CategoryId.of(1L), "음료"))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.DUPLICATE_CATEGORY_NAME_ERROR));
    }

    @Test
    public void 카테고리_이름_변경시_새_이름이_유효하지_않으면_INVALID_CATEGORY_ERROR를_던진다() {
        categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));

        assertThatThrownBy(() -> categoryService.updateName(CategoryId.of(1L), ""))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.INVALID_CATEGORY_ERROR));
    }

    @Test
    public void 카테고리_이름_변경중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.updateName(CategoryId.of(1L), "Coffee"))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── findAll ─────────────────────────────────────────────────────────────

    @Test
    public void 카테고리_전체_목록을_조회한다() {
        categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));
        categoryRepository.put(Category.of(CategoryId.of(2L), "음료", Instant.now()));

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
        categoryRepository.put(Category.of(CategoryId.of(1L), "아이스커피", Instant.now()));
        categoryRepository.put(Category.of(CategoryId.of(2L), "음료", Instant.now()));

        List<CategoryData> result = categoryService.searchByName("커피");

        assertThat(result).extracting(CategoryData::name).containsExactly("아이스커피");
    }

    @Test
    public void 이름으로_검색시_일치하는_카테고리가_없으면_빈_목록을_반환한다() {
        categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));

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
        Category category = categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));

        categoryService.remove(category.getId());

        assertThat(categoryRepository.contains(category.getId())).isFalse();
    }

    @Test
    public void 카테고리_삭제시_연결된_상품이_비활성화된다() {
        Category category = categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));
        productRepository.put(ProductFixture.builder().id(ProductId.of(1L)).categoryId(CategoryId.of(1L)).status(ProductStatus.ACTIVE).build());
        productRepository.put(ProductFixture.builder().id(ProductId.of(2L)).categoryId(CategoryId.of(1L)).status(ProductStatus.ACTIVE).build());

        categoryService.remove(category.getId());

        assertThat(productRepository.findAllByCategoryId(category.getId()))
                .allMatch(p -> p.getStatus() == ProductStatus.INACTIVE);
    }

    @Test
    public void 카테고리_삭제시_대상이_존재하지_않으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> categoryService.remove(CategoryId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    @Test
    public void 카테고리_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.put(Category.of(CategoryId.of(1L), "커피", Instant.now()));
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.remove(CategoryId.of(1L)))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }
}
