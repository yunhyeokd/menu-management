package com.dozycoffee.product.application.dto;

import com.dozycoffee.product.domain.OptionGroupId;

public record OptionGroupLinkSpec(
        OptionGroupId optionGroupId,
        boolean isRequired,
        boolean allowMultiple
) {}
