package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;

/**
 * Agregado raíz Clase (RF-02, US02).
 */
public class Clase {

    private static final int TEXTO_MIN = 3;
    private static final int TEXTO_MAX = 100;
    private static final int DURACION_MIN = 1;
    private static final int DURACION_MAX = 180;
    private static final int AFORO_MIN = 1;
    private static final int AFORO_MAX = 50;

    private final ClaseId id;
    private final String nombre;
    private final String instructor;
    private final Instant fechaInicio;
    private final int duracionMinutos;
    private final int aforoMaximo;
    private final EstadoClase estado;

    private Clase(ClaseId id, String nombre, String instructor, Instant fechaInicio, int duracionMinutos,
                  int aforoMaximo, EstadoClase estado) {
        this.id = id;
        this.nombre = nombre;
        this.instructor = instructor;
        this.fechaInicio = fechaInicio;
        this.duracionMinutos = duracionMinutos;
        this.aforoMaximo = aforoMaximo;
        this.estado = estado;
    }

    /**
     * Crea una clase nueva en estado PROGRAMADA (US02 CA-01).
     */
    public static Clase crear(String nombre, String instructor, Instant fechaInicio, int duracionMinutos,
                              int aforoMaximo, Clock clock) {
        Objects.requireNonNull(clock, "clock must not be null");
        validateNombre(nombre);
        validateInstructor(instructor);
        validateFechaInicio(fechaInicio, clock);
        validateDuracion(duracionMinutos);
        validateAforo(aforoMaximo);
        return new Clase(ClaseId.newId(), nombre, instructor, fechaInicio, duracionMinutos, aforoMaximo,
                EstadoClase.PROGRAMADA);
    }

    /**
     * Reconstruye una clase ya guardada, sin aplicar reglas. Solo para adaptadores de persistencia.
     */
    public static Clase restore(ClaseId id, String nombre, String instructor, Instant fechaInicio,
                                int duracionMinutos, int aforoMaximo, EstadoClase estado) {
        return new Clase(id, nombre, instructor, fechaInicio, duracionMinutos, aforoMaximo, estado);
    }

    // INV-16
    private static void validateNombre(String nombre) {
        validateTexto("nombre", nombre);
    }

    // INV-17
    private static void validateInstructor(String instructor) {
        validateTexto("instructor", instructor);
    }

    private static void validateTexto(String field, String value) {
        if (value == null) {
            throw new ValidationException(field + ": is required");
        }
        if (value.length() < TEXTO_MIN || value.length() > TEXTO_MAX) {
            throw new ValidationException(field + ": must have between 3 and 100 characters");
        }
        if (!value.equals(value.strip())) {
            throw new ValidationException(field + ": must not start or end with whitespace");
        }
    }

    // INV-06
    private static void validateFechaInicio(Instant fechaInicio, Clock clock) {
        if (fechaInicio == null) {
            throw new ValidationException("fechaInicio: is required");
        }
        if (!fechaInicio.isAfter(clock.instant())) {
            throw new ValidationException("fechaInicio: must be in the future");
        }
    }

    // INV-08
    private static void validateDuracion(int duracionMinutos) {
        if (duracionMinutos < DURACION_MIN || duracionMinutos > DURACION_MAX) {
            throw new ValidationException("duracionMinutos: must be between 1 and 180");
        }
    }

    // INV-07
    private static void validateAforo(int aforoMaximo) {
        if (aforoMaximo < AFORO_MIN || aforoMaximo > AFORO_MAX) {
            throw new ValidationException("aforoMaximo: must be between 1 and 50");
        }
    }

    public ClaseId getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getInstructor() {
        return instructor;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public int getAforoMaximo() {
        return aforoMaximo;
    }

    public EstadoClase getEstado() {
        return estado;
    }
}
