package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 409 CLASE_YA_COMENZADA (INV-10).
 */
public class ClaseYaComenzadaException extends DomainException {

    public ClaseYaComenzadaException(String claseId) {
        super("Class already started", List.of("claseId: " + claseId + " has already started"));
    }
}
