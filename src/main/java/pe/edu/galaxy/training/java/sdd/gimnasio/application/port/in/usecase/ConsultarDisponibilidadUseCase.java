package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarDisponibilidadQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.DisponibilidadResult;

/**
 * Caso de uso: consultar la disponibilidad de una clase (RF-05, US05).
 */
public interface ConsultarDisponibilidadUseCase {

    DisponibilidadResult consultar(ConsultarDisponibilidadQuery query);
}
