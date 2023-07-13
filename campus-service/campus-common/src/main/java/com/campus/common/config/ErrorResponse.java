package com.campus.common.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ErrorResponse {

    private String message;
    private Map<String,String> errors;
    private int code;

    public void addError(String field,String message) {
        if (errors == null) {
            errors = new HashMap<>();
        }
        errors.put(field,message);
    }
}






