package com.ecom.orderservice.exceptions;

public class ProductNotInCartException extends Exception{
    public ProductNotInCartException(String message) {
        super(message);
    }
}
