package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarReservasQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaPageResult;

/**
 * Caso de uso: consultar las reservas de un socio (RF-05, US05).
 */
public interface ConsultarReservasUseCase {

    ReservaPageResult consultar(ConsultarReservasQuery query);
}
