package com.example.fullstacktp1.exception;

import com.example.fullstacktp1.model.ApiError;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation error", System.currentTimeMillis(), validationErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolation = (ConstraintViolationException) cause;

            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    "",
                    System.currentTimeMillis()
            );
            // Identifier que l'erreur concerne une contrainte d'unicité
            if (constraintViolation.getConstraintName() != null && constraintViolation.getConstraintName().contains("UK")) {
                error.setMessage("Une catégorie avec ce nom existe déjà.");
            }
            // Vérifier si l'erreur est liée à une contrainte de clé étrangère
            else if (constraintViolation.getSQLException().getMessage().contains("FOREIGN KEY")) {
                error.setMessage("Impossible de supprimer cet élément car il est référencé par d'autres données.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Si ce n'est pas une contrainte spécifique
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Erreur d'intégrité des données.", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Une erreur inattendue est survenue.", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
