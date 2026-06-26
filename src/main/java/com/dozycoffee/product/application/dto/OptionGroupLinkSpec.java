package com.dozycoffee.application.product.dto;

import com.dozycoffee.product.domain.OptionGroupId;

public record OptionGroupLinkSpec(
        OptionGroupId optionGroupId,
        boolean isRequired,
        boolean allowMultiple
) {}
