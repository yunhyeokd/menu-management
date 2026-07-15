package com.dozycoffee.catalog.presentation;

import com.dozycoffee.infrastructure.web.interceptor.RequireRole;
import com.dozycoffee.catalog.application.dto.TagData;
import com.dozycoffee.catalog.application.service.tag.TagService;
import com.dozycoffee.catalog.application.usecase.DeleteTagUseCase;
import com.dozycoffee.catalog.domain.TagId;
import com.dozycoffee.catalog.presentation.dto.TagCreateRequest;
import com.dozycoffee.catalog.presentation.dto.TagRenameRequest;
import com.dozycoffee.catalog.presentation.dto.TagResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final DeleteTagUseCase deleteTagUseCase;

    @GetMapping
    @RequireRole({"SYSTEM", "ADMIN", "BRANCH"})
    public ResponseEntity<List<TagResponse>> findTags(@RequestParam(required = false) String name) {
        List<TagData> tags = (name == null || name.isBlank()) ? tagService.findAll() : tagService.searchByName(name);
        List<TagResponse> response = tags.stream().map(TagResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagCreateRequest request) {
        TagData tag = tagService.create(request.name());
        return ResponseEntity.ok(TagResponse.from(tag));
    }

    @PatchMapping("/{tagId}")
    @RequireRole({"SYSTEM", "ADMIN"})
    public ResponseEntity<Void> renameTag(@PathVariable TagId tagId, @Valid @RequestBody TagRenameRequest request) {
        tagService.changeTagName(tagId, request.name());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tagId}")
    @RequireRole({"ADMIN"})
    public ResponseEntity<Void> deleteTag(@PathVariable TagId tagId) {
        deleteTagUseCase.execute(tagId);
        return ResponseEntity.noContent().build();
    }
}
