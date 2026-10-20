package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command;

import java.time.Instant;

/**
 * Entrada del caso de uso crear clase (US02).
 */
public record CrearClaseCommand(String nombre, String instructor, Instant fechaInicio, int duracionMinutos,
                                int aforoMaximo) {
}
