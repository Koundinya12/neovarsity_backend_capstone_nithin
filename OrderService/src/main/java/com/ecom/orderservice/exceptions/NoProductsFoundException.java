package com.ecom.orderservice.exceptions;

public class NoProductsFoundException extends Exception{
    public NoProductsFoundException(String message) {
        super(message);
    }
}
