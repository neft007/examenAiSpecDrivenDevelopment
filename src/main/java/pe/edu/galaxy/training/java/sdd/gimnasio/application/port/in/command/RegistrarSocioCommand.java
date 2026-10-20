package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command;

/**
 * Entrada del caso de uso registrar socio (US01).
 */
public record RegistrarSocioCommand(String dni, String nombre, String email) {
}
