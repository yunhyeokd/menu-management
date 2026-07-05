package com.dozycoffee.branch.domain;

import com.dozycoffee.core.id.StringIdentifier;

import java.util.regex.Pattern;

public class BranchCode extends StringIdentifier {

    private static final Pattern CODE_PATTERN =
            Pattern.compile("^[0-9]{8}$");

    private BranchCode(String value) { super(value); }  // null 검사는 부모가

    @Override
    protected void validate(String value) {
        if (value == null || !CODE_PATTERN.matcher(value).matches()) {
            throw new BranchException("Invalid branch code: " + value);
        }
    }

    public static BranchCode of(String value) {
        return new BranchCode(value);
    }
}
