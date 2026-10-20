package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * Base de las excepciones de dominio. Cada subclase corresponde a una fila de ADR-0004.
 */
public abstract class DomainException extends RuntimeException {

    private final List<String> details;

    protected DomainException(String message, List<String> details) {
        super(message);
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public List<String> getDetails() {
        return details;
    }
}
