package com.ecom.orderservice.exceptions;

public class CartNotFoundException extends Exception{
    public CartNotFoundException(String message) {
        super(message);
    }
}
