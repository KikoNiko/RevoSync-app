package com.kbc.kibi_coins.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCategoryException extends RuntimeException{
    private final String message;
    public InvalidCategoryException(String message) {
        this.message = message;
    }
}
