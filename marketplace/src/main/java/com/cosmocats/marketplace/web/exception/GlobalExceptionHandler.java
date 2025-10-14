package com.cosmocats.marketplace.web.exception;

import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        List<ValidationField> issues = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ValidationField(fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("/problems/validation-error"));
        problem.setTitle("Validation Failed");
        problem.setDetail("Validation failed for one or more fields");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("invalid-params", issues);
        problem.setProperty("timestamp", OffsetDateTime.now().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/problem+json");
        return new ResponseEntity<>(problem, headers, HttpStatus.BAD_REQUEST);
    }

    // Product not found -> 404 with ProblemDetail (specific exception)
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProductNotFound(ProductNotFoundException ex,
                                                               HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setType(URI.create("/problems/product-not-found"));
        problem.setTitle("Product Not Found");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("productId", ex.getProductId());
        problem.setProperty("timestamp", OffsetDateTime.now().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/problem+json");
        return new ResponseEntity<>(problem, headers, HttpStatus.NOT_FOUND);
    }

    // Generic fallback -> 500 internal server error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAll(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setType(URI.create("/problems/internal-server-error"));
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred. Please contact support.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", OffsetDateTime.now().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/problem+json");
        return new ResponseEntity<>(problem, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private record ValidationField(String field, Object rejectedValue, String message) {}
}

