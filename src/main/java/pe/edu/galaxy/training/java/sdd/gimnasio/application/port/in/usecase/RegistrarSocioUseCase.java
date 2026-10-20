package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.RegistrarSocioCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.SocioResult;

/**
 * Caso de uso: registrar socio (RF-01, US01).
 */
public interface RegistrarSocioUseCase {

    SocioResult registrar(RegistrarSocioCommand command);
}
