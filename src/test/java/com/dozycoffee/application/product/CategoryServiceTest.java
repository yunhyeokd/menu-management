package com.dozycoffee.application.product;

import com.dozycoffee.application.product.mock.MockCategoryRepository;
import com.dozycoffee.domain.product.Category;
import com.dozycoffee.domain.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CategoryServiceTest {

    CategoryRepository categoryRepository;
    CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        categoryRepository = new MockCategoryRepository();
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    public void 카테고리를_생성한다() {
        String name = "커피";
        Category category = categoryService.createCategory(name);
        assertThat(categoryRepository.findById(category.getId())).isNotNull();
    }

    @Test
    public void 카테고리_이름이_중복되면_생성에_실패한다() {
        String name = "커피";
        Category category = categoryService.createCategory(name);
        assertThatThrownBy(() -> categoryService.createCategory(category.getName()))
                .isInstanceOf(CategoryServiceException.class);
    }

    @Test
    public void 카테고리_이름을_변경한다() {
        String name = "커피";
        Category category = categoryService.createCategory(name);
        String newName = "Coffee";
        Category updatedCategory = categoryService.updateName(category.getId(), newName);
        assertThat(updatedCategory.getName()).isEqualTo(newName);
    }

    @Test
    public void 카테고리를_삭제한다() {
        String name = "커피";
        Category category = categoryService.createCategory(name);
        assertThat(categoryRepository.findById(category.getId())).isNotNull();
        categoryService.removeCategory(category.getId());
        assertThat(categoryRepository.findById(category.getId())).isNull();
    }

    @Test
    public void 카테고리_삭제시_해당_카테고리_상품들은_비활성화된다() {

    }

}
