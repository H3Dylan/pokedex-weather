package dev.pokedex.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Erreurs de validation (ex: @NotBlank, @Min)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation failed");
        body.put("details", errors);

        return ResponseEntity.badRequest().body(body);
    }

    // Erreurs d’unicité ou contrainte (ex: nom déjà existant)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleConstraint(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Constraint violation");
        body.put("message", "Probably a duplicate name or invalid relation");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // Erreur 404 si l’élément n’existe pas
    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElement(java.util.NoSuchElementException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Found");
        body.put("message", "Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
