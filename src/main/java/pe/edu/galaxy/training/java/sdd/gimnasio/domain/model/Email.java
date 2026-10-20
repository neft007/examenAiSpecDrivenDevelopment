package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.util.regex.Pattern;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;

/**
 * Email con formato válido (INV-04).
 */
public record Email(String value) {

    private static final Pattern FORMAT = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public Email {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new ValidationException("email: must be a valid email address");
        }
    }
}
