package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.dto.BranchAuthKeyReissueResult;

public record BranchAuthKeyReissueResponse(
        String auth_key
) {

    public static BranchAuthKeyReissueResponse from(BranchAuthKeyReissueResult result) {
        return new BranchAuthKeyReissueResponse(result.rawAuthKey());
    }

}
