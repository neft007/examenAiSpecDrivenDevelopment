package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query;

import java.util.UUID;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;

/**
 * Entrada del caso de uso consultar reservas (US05). estado es opcional: null significa sin filtro.
 */
public record ConsultarReservasQuery(UUID socioId, EstadoReserva estado) {
}
