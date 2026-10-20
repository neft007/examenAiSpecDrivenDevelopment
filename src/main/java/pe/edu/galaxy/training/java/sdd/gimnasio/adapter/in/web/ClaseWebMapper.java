package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ClaseResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.CrearClaseRequest;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.EstadoClase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CrearClaseCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ClaseResult;

/**
 * Traduce entre los DTO web del contrato y los modelos de aplicación de clase (architecture.md §3).
 */
@Component
public class ClaseWebMapper {

    // Los campos obligatorios ya fueron validados con @NotNull por el DTO generado
    public CrearClaseCommand toCommand(CrearClaseRequest request) {
        return new CrearClaseCommand(
                request.getNombre(),
                request.getInstructor(),
                request.getFechaInicio().toInstant(),
                request.getDuracionMinutos(),
                request.getAforoMaximo());
    }

    public ClaseResponse toResponse(ClaseResult result) {
        return new ClaseResponse(
                result.id(),
                result.nombre(),
                result.instructor(),
                result.fechaInicio().atOffset(ZoneOffset.UTC),
                result.duracionMinutos(),
                result.aforoMaximo(),
                EstadoClase.valueOf(result.estado().name()));
    }
}
