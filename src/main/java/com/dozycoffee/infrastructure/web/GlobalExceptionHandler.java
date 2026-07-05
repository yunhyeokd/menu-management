package com.dozycoffee.infrastructure.web;

import com.dozycoffee.core.exception.service.AuthenticationException;
import com.dozycoffee.core.exception.service.AuthorizationException;
import com.dozycoffee.core.exception.service.CommonErrors;
import com.dozycoffee.core.exception.service.CommonServiceCode;
import com.dozycoffee.core.exception.service.ConflictException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.ServiceException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.core.exception.service.ValidationException;
import com.dozycoffee.infrastructure.web.dto.ErrorResponse;
import com.dozycoffee.infrastructure.web.dto.FieldErrorDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.from(ex));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.from(ex));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(AuthorizationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.from(ex));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.from(ex));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.from(ex));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handleSystem(SystemException ex) {
        log.error("System exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.from(ex));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleService(ServiceException ex) {
        log.error("Unclassified service exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.from(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleRequestValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();
        ErrorResponse response = ErrorResponse.of(
                CommonServiceCode.COMMON, CommonErrors.REQUEST_VALIDATION_FAILED, fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        ErrorResponse response = ErrorResponse.of(CommonServiceCode.COMMON, CommonErrors.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
