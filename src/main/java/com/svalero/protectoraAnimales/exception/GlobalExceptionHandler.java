package com.svalero.protectoraAnimales.exception;

import com.svalero.protectoraAnimales.controller.AdoptionController;
import com.svalero.protectoraAnimales.exception.runtime.NoChangeException;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(AdoptionController.class);

    // region excepciones generales

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

    // 400: No se puede borrar una ubicación con animales asociados, entre otros.
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> illegalStateException(IllegalStateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(400, ex.getMessage(), null);

        logger.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
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
    // endregion

    // region excepciones específicas:

    // 400: Comprobaciones de datos que no necesitan/deben cambiar
    @org.springframework.web.bind.annotation.ExceptionHandler(NoChangeException.class)
    public ResponseEntity<ErrorResponse> NoChangeException(NoChangeException noChangeEx){
        ErrorResponse errorResponse = new ErrorResponse(400, noChangeEx.getMessage(), null);

        logger.error(noChangeEx.getMessage(), noChangeEx);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 400: Tipos no coinciden (ej: pasas un string y espera un long)
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException typeMismatchEx) {
        ErrorResponse errorResponse = new ErrorResponse(400, typeMismatchEx.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    // endregion
}
