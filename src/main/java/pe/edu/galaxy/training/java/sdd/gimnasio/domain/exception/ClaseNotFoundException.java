package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 404 CLASE_NOT_FOUND.
 */
public class ClaseNotFoundException extends DomainException {

    public ClaseNotFoundException(String claseId) {
        super("Clase not found", List.of("claseId: " + claseId + " does not exist"));
    }
}
