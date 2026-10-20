package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result;

import java.util.UUID;

/**
 * Disponibilidad de una clase (RF-05: plazas disponibles = aforo máximo - reservas confirmadas).
 */
public record DisponibilidadResult(UUID claseId, int aforoMaximo, long reservasConfirmadas, int plazasDisponibles) {
}
