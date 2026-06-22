package com.dozycoffee.application.product.service;

public enum ProductErrors {

    UNKNOWN_ERROR(1, "Unknown error"),

    INVALID_PRODUCT_ERROR(2, "Invalid product"),
    PRODUCT_NOT_FOUND_ERROR(3, "Product not found"),

    INVALID_CATEGORY_ERROR(10, "Invalid category"),
    DUPLICATE_CATEGORY_NAME_ERROR(11, "Duplicate category name"),
    CATEGORY_NOT_FOUND_ERROR(12, "Category not found"),

    INVALID_TAG_ERROR(20, "Invalid tag"),
    DUPLICATE_TAG_NAME_ERROR(21, "Duplicate tag name"),
    TAG_NOT_FOUND_ERROR(22, "Tag not found"),

    INVALID_OPTION_ERROR(30, "Invalid option"),
    OPTION_GROUP_NOT_FOUND_ERROR(31, "Option group not found"),
    OPTION_ITEM_NOT_FOUND_ERROR(32, "Option item not found"),
    EMPTY_OPTION_GROUP_ERROR(33, "No option group items"),
    LINKED_PRODUCT_EXISTS_ERROR(34, "Linked products exist"),
    ;

    public final int errorCode;
    public final String message;

    ProductErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
