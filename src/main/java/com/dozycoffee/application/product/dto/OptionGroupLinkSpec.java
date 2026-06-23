package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.OptionGroupId;

public record OptionGroupLinkSpec(
        OptionGroupId optionGroupId,
        boolean isRequired,
        boolean allowMultiple
) {}
