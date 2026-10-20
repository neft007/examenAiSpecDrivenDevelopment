package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;

/**
 * Agregado raíz Socio (RF-01, US01).
 */
public class Socio {

    private static final int NOMBRE_MIN = 3;
    private static final int NOMBRE_MAX = 100;

    private final SocioId id;
    private final Dni dni;
    private final String nombre;
    private final Email email;
    private Membresia membresia;
    private final Instant fechaAlta;

    private Socio(SocioId id, Dni dni, String nombre, Email email, Membresia membresia, Instant fechaAlta) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.membresia = membresia;
        this.fechaAlta = fechaAlta;
    }

    /**
     * Registra un socio nuevo con membresía ACTIVA (US01 CA-01).
     */
    public static Socio registrar(Dni dni, String nombre, Email email, Clock clock) {
        Objects.requireNonNull(dni, "dni must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(clock, "clock must not be null");
        validateNombre(nombre);
        return new Socio(SocioId.newId(), dni, nombre, email, Membresia.ACTIVA, Instant.now(clock));
    }

    /**
     * Reconstruye un socio ya guardado, sin aplicar reglas. Solo para adaptadores de persistencia.
     */
    public static Socio restore(SocioId id, Dni dni, String nombre, Email email, Membresia membresia,
                                Instant fechaAlta) {
        return new Socio(id, dni, nombre, email, membresia, fechaAlta);
    }

    // INV-05
    private static void validateNombre(String nombre) {
        if (nombre == null) {
            throw new ValidationException("nombre: is required");
        }
        if (nombre.length() < NOMBRE_MIN || nombre.length() > NOMBRE_MAX) {
            throw new ValidationException("nombre: must have between 3 and 100 characters");
        }
        if (!nombre.equals(nombre.strip())) {
            throw new ValidationException("nombre: must not start or end with whitespace");
        }
    }

    public SocioId getId() {
        return id;
    }

    public Dni getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public Email getEmail() {
        return email;
    }

    public Membresia getMembresia() {
        return membresia;
    }

    public Instant getFechaAlta() {
        return fechaAlta;
    }
}
