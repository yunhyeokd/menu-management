package com.dozycoffee.application.product.service;

public enum ProductErrors {

    UNKNOWN_ERROR(1, "Unknown error"),
    INVALID_PRODUCT_ERROR(2, "Invalid product"),
    NOT_FOUND_ERROR(3, "Product not found"),
    ;

    public final int errorCode;
    public final String message;

    ProductErrors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
