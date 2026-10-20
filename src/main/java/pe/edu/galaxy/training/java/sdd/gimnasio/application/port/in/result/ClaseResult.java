package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result;

import java.time.Instant;
import java.util.UUID;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoClase;

/**
 * Salida de los casos de uso de clase. El dominio nunca sale de la aplicación.
 */
public record ClaseResult(UUID id, String nombre, String instructor, Instant fechaInicio, int duracionMinutos,
                          int aforoMaximo, EstadoClase estado) {

    public static ClaseResult from(Clase clase) {
        return new ClaseResult(
                clase.getId().value(),
                clase.getNombre(),
                clase.getInstructor(),
                clase.getFechaInicio(),
                clase.getDuracionMinutos(),
                clase.getAforoMaximo(),
                clase.getEstado());
    }
}
