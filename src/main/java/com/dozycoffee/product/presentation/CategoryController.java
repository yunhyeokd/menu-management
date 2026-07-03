package com.dozycoffee.product.presentation;

import com.dozycoffee.product.application.dto.CategoryData;
import com.dozycoffee.product.application.service.category.CategoryService;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.presentation.dto.CategoryCreateRequest;
import com.dozycoffee.product.presentation.dto.CategoryResponse;
import com.dozycoffee.product.presentation.dto.CategoryUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> searchCategory(
            @RequestParam(required = false) String name
    ) {
        List<CategoryData> categories = (name == null || name.isBlank()) ? categoryService.findAll() : categoryService.searchByName(name);
        List<CategoryResponse> response = categories.stream().map(CategoryResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable CategoryId categoryId
    ) {
        CategoryData category = categoryService.findById(categoryId);
        CategoryResponse response = CategoryResponse.from(category);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest request) {
        CategoryData result = categoryService.create(request.name());
        CategoryResponse response = CategoryResponse.from(result);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable CategoryId categoryId,
            @RequestBody CategoryUpdateRequest request
    ) {
        CategoryData category = categoryService.updateName(categoryId, request.name());
        CategoryResponse response = CategoryResponse.from(category);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable CategoryId categoryId
    ) {
        categoryService.remove(categoryId);
        return ResponseEntity.ok().build();
    }

}
