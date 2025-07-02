package com.tia.lms_backend.exception;


import com.tia.lms_backend.dto.response.GeneralResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GeneralExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GeneralExceptionHandler.class);

    /**
     * EntityAlreadyExistsException
     * */
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<GeneralResponse<?>> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        GeneralResponse<?> errorResponse = GeneralResponse.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .data(null)
                .build();
        log.error("{}:{}", errorResponse.getMessage(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    /**
     * Validasyon hatalarını yakalayıp GeneralResponse formatında döner
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation error: {}", errors);

        GeneralResponse<Map<String, String>> response = GeneralResponse.<Map<String, String>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Validasyon hatası")
                .data(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GeneralResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        log.error("IllegalArgumentException: {}", ex.getMessage());

        GeneralResponse<Void> response = GeneralResponse.<Void>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<GeneralResponse<?>> handleGeneralException(GeneralException ex) {
        GeneralResponse<?> errorResponse = GeneralResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .data(null)
                .build();
        log.error("{}:{}", errorResponse.getMessage(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<GeneralResponse<?>> handleGeneralException(Exception ex) {
//        GeneralResponse<?> errorResponse = GeneralResponse.builder()
//                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message(ex.getMessage())
//                .data(null)
//                .build();
//        log.error("{}:{}", errorResponse.getMessage(), ex.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    /**
     * ENTITY NOT FOUND
     * */

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GeneralResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex) {
        GeneralResponse<?> errorResponse = GeneralResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .data(null)
                .build();
        log.error("{}:{}", errorResponse.getMessage(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    /**
     * EntityAlreadyExistsException
     * */
//    @ExceptionHandler(EntityAlreadyExistsException.class)
//    public ResponseEntity<GeneralResponse<?>> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
//        GeneralResponse<?> errorResponse = GeneralResponse.builder()
//                .code(HttpStatus.CONFLICT.value())
//                .message(ex.getMessage())
//                .data(null)
//                .build();
//        log.error("{}:{}", errorResponse.getMessage(), ex.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
//    }

}
