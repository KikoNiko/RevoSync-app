package com.kbc.kibi_coins.controller;

import com.kbc.kibi_coins.util.InvalidCategoryException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<?> handleInvalidCategoryException(InvalidCategoryException ice) {
        return ResponseEntity.badRequest().body(ice.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(err -> {
                    errorMap.put(err.getField(), err.getDefaultMessage());
                });
        return errorMap;
    }
}
