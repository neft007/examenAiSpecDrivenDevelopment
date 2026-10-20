package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.ReservarPlazaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;

/**
 * Caso de uso: reservar plaza en una clase (RF-03, US03).
 */
public interface ReservarPlazaUseCase {

    ReservaResult reservar(ReservarPlazaCommand command);
}
