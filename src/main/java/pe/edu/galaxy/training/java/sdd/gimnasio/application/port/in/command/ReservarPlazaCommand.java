package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command;

import java.util.UUID;

/**
 * Entrada del caso de uso reservar plaza (US03).
 */
public record ReservarPlazaCommand(UUID socioId, UUID claseId) {
}
