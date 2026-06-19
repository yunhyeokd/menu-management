package com.dozycoffee.domain.branch;

import com.dozycoffee.domain.common.StringIdentifier;

import java.util.regex.Pattern;

public class BranchCode extends StringIdentifier {

    private static final Pattern CODE_PATTERN =
            Pattern.compile("^[0-9]{8}$");

    private BranchCode(String value) { super(value); }  // null 검사는 부모가

    public static BranchCode of(String value) {
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new BranchException("Invalid branch code: " + value);
        }
        return new BranchCode(value);
    }
}
