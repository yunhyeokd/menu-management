package com.dozycoffee.application.product;

import com.dozycoffee.application.product.service.category.CategoryErrors;
import com.dozycoffee.application.product.service.category.CategoryService;
import com.dozycoffee.application.product.service.category.CategoryServiceException;
import com.dozycoffee.domain.product.Category;
import com.dozycoffee.domain.product.ProductFixture;
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

    @BeforeEach
    public void setUp() {
        categoryRepository = new FakeCategoryRepository();
        productRepository = new FakeProductRepository();
        categoryService = new CategoryService(categoryRepository, productRepository);
    }

    // ─── create ──────────────────────────────────────────────────────────────

    @Test
    public void 카테고리를_정상_생성한다() {
        Category created = categoryService.create("커피");

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("커피");
        assertThat(categoryRepository.findById(created.getId())).isEqualTo(created);
    }

    @Test
    public void 카테고리_생성시_이름이_중복되면_DUPLICATE_NAME_ERROR를_던진다() {
        categoryRepository.put(Category.of(1L, "커피", Instant.now()));

        assertThatThrownBy(() -> categoryService.create("커피"))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.DUPLICATE_NAME_ERROR.errorCode));
    }

    @Test
    public void 카테고리_생성시_이름이_유효하지_않으면_INVALID_CATEGORY_ERROR를_던진다() {
        assertThatThrownBy(() -> categoryService.create(""))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.INVALID_CATEGORY_ERROR.errorCode));
    }

    @Test
    public void 카테고리_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.create("커피"))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── updateName ──────────────────────────────────────────────────────────

    @Test
    public void 카테고리_이름을_정상_변경한다() {
        Category category = categoryRepository.put(Category.of(1L, "커피", Instant.now()));

        categoryService.updateName(category.getId(), "Coffee");

        assertThat(categoryRepository.findById(category.getId()).getName()).isEqualTo("Coffee");
    }

    @Test
    public void 카테고리_이름_변경시_대상이_존재하지_않으면_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> categoryService.updateName(999L, "Coffee"))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 카테고리_이름_변경시_새_이름이_중복되면_DUPLICATE_NAME_ERROR를_던진다() {
        categoryRepository.put(Category.of(1L, "커피", Instant.now()));
        categoryRepository.put(Category.of(2L, "음료", Instant.now()));

        assertThatThrownBy(() -> categoryService.updateName(1L, "음료"))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.DUPLICATE_NAME_ERROR.errorCode));
    }

    @Test
    public void 카테고리_이름_변경시_새_이름이_유효하지_않으면_INVALID_CATEGORY_ERROR를_던진다() {
        categoryRepository.put(Category.of(1L, "커피", Instant.now()));

        assertThatThrownBy(() -> categoryService.updateName(1L, ""))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.INVALID_CATEGORY_ERROR.errorCode));
    }

    @Test
    public void 카테고리_이름_변경중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.put(Category.of(1L, "커피", Instant.now()));
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.updateName(1L, "Coffee"))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── findAll ─────────────────────────────────────────────────────────────

    @Test
    public void 카테고리_전체_목록을_조회한다() {
        categoryRepository.put(Category.of(1L, "커피", Instant.now()));
        categoryRepository.put(Category.of(2L, "음료", Instant.now()));

        List<Category> categories = categoryService.findAll();

        assertThat(categories).hasSize(2);
    }

    @Test
    public void 카테고리가_없으면_빈_목록을_반환한다() {
        List<Category> categories = categoryService.findAll();

        assertThat(categories).isEmpty();
    }

    @Test
    public void 카테고리_전체_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.findAll())
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── searchByName ────────────────────────────────────────────────────────

    @Test
    public void 이름으로_카테고리를_검색한다() {
        categoryRepository.put(Category.of(1L, "아이스커피", Instant.now()));
        categoryRepository.put(Category.of(2L, "음료", Instant.now()));

        List<Category> result = categoryService.searchByName("커피");

        assertThat(result).extracting(Category::getName).containsExactly("아이스커피");
    }

    @Test
    public void 이름으로_검색시_일치하는_카테고리가_없으면_빈_목록을_반환한다() {
        categoryRepository.put(Category.of(1L, "커피", Instant.now()));

        List<Category> result = categoryService.searchByName("존재하지않음");

        assertThat(result).isEmpty();
    }

    @Test
    public void 이름으로_카테고리_검색중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.searchByName("커피"))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── remove ──────────────────────────────────────────────────────────────

    @Test
    public void 카테고리를_정상_삭제한다() {
        Category category = categoryRepository.put(Category.of(1L, "커피", Instant.now()));

        categoryService.remove(category.getId());

        assertThat(categoryRepository.contains(category.getId())).isFalse();
    }

    @Test
    public void 카테고리_삭제시_연결된_상품이_비활성화된다() {
        Category category = categoryRepository.put(Category.of(1L, "커피", Instant.now()));
        productRepository.put(ProductFixture.builder().id(1L).categoryId(1L).status(ProductStatus.ACTIVE).build());
        productRepository.put(ProductFixture.builder().id(2L).categoryId(1L).status(ProductStatus.ACTIVE).build());

        categoryService.remove(category.getId());

        assertThat(productRepository.findAllByCategoryId(category.getId()))
                .allMatch(p -> p.getStatus() == ProductStatus.INACTIVE);
    }

    @Test
    public void 카테고리_삭제시_대상이_존재하지_않으면_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> categoryService.remove(999L))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 카테고리_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        categoryRepository.put(Category.of(1L, "커피", Instant.now()));
        categoryRepository.throwOnNextCall();

        assertThatThrownBy(() -> categoryService.remove(1L))
                .isInstanceOf(CategoryServiceException.class)
                .satisfies(e -> assertThat(((CategoryServiceException) e).getErrorCode())
                        .isEqualTo(CategoryErrors.UNKNOWN_ERROR.errorCode));
    }
}
