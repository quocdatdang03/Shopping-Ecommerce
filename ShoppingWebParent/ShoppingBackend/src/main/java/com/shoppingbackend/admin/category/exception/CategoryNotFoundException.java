package com.shoppingbackend.admin.category.exception;

import com.shoppingbackend.admin.user.exception.UserNotFoundException;

public class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
