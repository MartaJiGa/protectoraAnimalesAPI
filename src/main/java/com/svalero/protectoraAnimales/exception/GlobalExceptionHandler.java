package com.svalero.protectoraAnimales.exception;

import com.svalero.protectoraAnimales.controller.AdoptionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(AdoptionController.class);

    // 400
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationExceptions(MethodArgumentNotValidException argNotValidEx) {
        Map<String, String> errors = new HashMap<>();
        argNotValidEx.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorResponse errorResponse = new ErrorResponse(400, "Errores de validación", errors);

        logger.error(argNotValidEx.getMessage(), argNotValidEx);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 404
    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException resNotFoundEx){
        ErrorResponse errorResponse = new ErrorResponse(404, resNotFoundEx.getMessage(), null);

        logger.error(resNotFoundEx.getMessage(), resNotFoundEx);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 500
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generalException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(500, "Error interno del servidor. Inténtalo más tarde.", null);

        logger.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
