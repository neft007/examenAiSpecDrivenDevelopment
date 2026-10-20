package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query;

import java.util.UUID;

/**
 * Entrada del caso de uso consultar disponibilidad (US05).
 */
public record ConsultarDisponibilidadQuery(UUID claseId) {
}
