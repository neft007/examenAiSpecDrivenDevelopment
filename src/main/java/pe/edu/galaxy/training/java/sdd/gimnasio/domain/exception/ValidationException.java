package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 400 VALIDATION_ERROR (formato, longitud, rango, valor fuera de catálogo).
 */
public class ValidationException extends DomainException {

    public ValidationException(String detail) {
        this(List.of(detail));
    }

    public ValidationException(List<String> details) {
        super("Validation failed", details);
    }
}
