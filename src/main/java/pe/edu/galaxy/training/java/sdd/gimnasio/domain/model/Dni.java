package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.util.regex.Pattern;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;

/**
 * DNI de 8 dígitos (INV-01).
 */
public record Dni(String value) {

    private static final Pattern FORMAT = Pattern.compile("^\\d{8}$");

    public Dni {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new ValidationException("dni: must have exactly 8 digits (e.g. 12345678)");
        }
    }
}
