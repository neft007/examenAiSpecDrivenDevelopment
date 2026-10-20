package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command;

import java.util.UUID;

/**
 * Entrada del caso de uso cancelar reserva (US04).
 */
public record CancelarReservaCommand(UUID reservaId) {
}
