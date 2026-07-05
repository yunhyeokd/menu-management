package com.dozycoffee.product.presentation;

import com.dozycoffee.product.application.dto.OptionGroupData;
import com.dozycoffee.product.application.service.option.OptionService;
import com.dozycoffee.product.application.usecase.DeleteOptionGroupUseCase;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.presentation.dto.OptionGroupCreateRequest;
import com.dozycoffee.product.presentation.dto.OptionGroupItemsUpdateRequest;
import com.dozycoffee.product.presentation.dto.OptionGroupProfileUpdateRequest;
import com.dozycoffee.product.presentation.dto.OptionGroupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;
    private final DeleteOptionGroupUseCase deleteOptionGroupUseCase;

    @GetMapping
    public ResponseEntity<List<OptionGroupResponse>> findOptionGroups() {
        List<OptionGroupData> optionGroups = optionService.findAll();
        List<OptionGroupResponse> response = optionGroups.stream().map(OptionGroupResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OptionGroupResponse> createOptionGroup(@Valid @RequestBody OptionGroupCreateRequest request) {
        OptionGroupData optionGroup = optionService.create(request.toCommand());
        return ResponseEntity.ok(OptionGroupResponse.from(optionGroup));
    }

    @PatchMapping("/{optionGroupId}/profile")
    public ResponseEntity<Void> updateOptionGroupProfile(
            @PathVariable OptionGroupId optionGroupId,
            @Valid @RequestBody OptionGroupProfileUpdateRequest request
    ) {
        optionService.updateOptionGroupProfile(optionGroupId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{optionGroupId}/items")
    public ResponseEntity<Void> updateOptionGroupItems(
            @PathVariable OptionGroupId optionGroupId,
            @Valid @RequestBody OptionGroupItemsUpdateRequest request
    ) {
        optionService.updateOptionGroupItems(optionGroupId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{optionGroupId}")
    public ResponseEntity<Void> deleteOptionGroup(@PathVariable OptionGroupId optionGroupId) {
        deleteOptionGroupUseCase.execute(optionGroupId);
        return ResponseEntity.noContent().build();
    }
}
