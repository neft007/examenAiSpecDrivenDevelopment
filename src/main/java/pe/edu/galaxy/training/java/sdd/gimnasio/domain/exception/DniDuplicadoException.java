package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 409 DNI_DUPLICADO (INV-02).
 */
public class DniDuplicadoException extends DomainException {

    public DniDuplicadoException(String dni) {
        super("DNI already registered", List.of("dni: " + dni + " already exists"));
    }
}
