package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.dto.BranchAuthKeyReissueResult;

public record BranchAuthKeyReissueResponse(
        String authKey
) {

    public static BranchAuthKeyReissueResponse from(BranchAuthKeyReissueResult result) {
        return new BranchAuthKeyReissueResponse(result.rawAuthKey());
    }

}
