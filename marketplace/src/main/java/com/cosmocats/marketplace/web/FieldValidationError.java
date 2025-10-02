package com.cosmocats.marketplace.web;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldValidationError {
    private String field;
    private Object rejectedValue;
    private String message;
}
