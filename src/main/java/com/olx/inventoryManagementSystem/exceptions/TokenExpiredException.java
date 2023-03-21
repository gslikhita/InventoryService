package com.olx.inventoryManagementSystem.exceptions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TokenExpiredException extends Exception{
    public TokenExpiredException(String message) {
        super(message);
    }
}
