package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 409 EMAIL_DUPLICADO (INV-03).
 */
public class EmailDuplicadoException extends DomainException {

    public EmailDuplicadoException(String email) {
        super("Email already registered", List.of("email: " + email + " already exists"));
    }
}
