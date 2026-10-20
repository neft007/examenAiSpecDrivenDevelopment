package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CancelarReservaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;

/**
 * Caso de uso: cancelar reserva (RF-04, US04).
 */
public interface CancelarReservaUseCase {

    ReservaResult cancelar(CancelarReservaCommand command);
}
