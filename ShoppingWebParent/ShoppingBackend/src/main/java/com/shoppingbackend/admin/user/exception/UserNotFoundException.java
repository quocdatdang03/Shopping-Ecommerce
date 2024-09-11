package com.shoppingbackend.admin.user.exception;

// Custom Exception
public class UserNotFoundException extends Exception{

    public UserNotFoundException (String message) {
        super(message);
    }
}
