package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CrearClaseCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ClaseResult;

/**
 * Caso de uso: crear clase (RF-02, US02).
 */
public interface CrearClaseUseCase {

    ClaseResult crear(CrearClaseCommand command);
}
