package com.ecom.userservice.exceptions;

public class UsernameNotFoundException extends Exception{
    public UsernameNotFoundException(String message) {
        super(message);
    }
}
