package com.ecom.productservice.exceptions;

public class NoProductsFoundException extends Exception{
    public NoProductsFoundException(String message) {
        super(message);
    }
}
