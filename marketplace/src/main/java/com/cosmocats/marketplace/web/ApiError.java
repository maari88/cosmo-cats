package com.cosmocats.marketplace.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String type;
    private String title;
    private int status;
    private String error;
    private String detail;
    private String instance;
    private OffsetDateTime timestamp;
    private List<FieldValidationError> errors;
}
