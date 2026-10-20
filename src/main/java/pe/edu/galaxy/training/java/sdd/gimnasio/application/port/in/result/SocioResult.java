package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result;

import java.time.Instant;
import java.util.UUID;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Membresia;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;

/**
 * Salida de los casos de uso de socio. El dominio nunca sale de la aplicación.
 */
public record SocioResult(UUID id, String dni, String nombre, String email, Membresia membresia,
                          Instant fechaAlta) {

    public static SocioResult from(Socio socio) {
        return new SocioResult(
                socio.getId().value(),
                socio.getDni().value(),
                socio.getNombre(),
                socio.getEmail().value(),
                socio.getMembresia(),
                socio.getFechaAlta());
    }
}
