package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.EstadoMembresia;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.RegistrarSocioRequest;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.SocioResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.RegistrarSocioCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.SocioResult;

/**
 * Traduce entre los DTO web del contrato y los modelos de aplicación de socio (architecture.md §3).
 */
@Component
public class SocioWebMapper {

    public RegistrarSocioCommand toCommand(RegistrarSocioRequest request) {
        return new RegistrarSocioCommand(request.getDni(), request.getNombre(), request.getEmail());
    }

    public SocioResponse toResponse(SocioResult result) {
        return new SocioResponse(
                result.id(),
                result.dni(),
                result.nombre(),
                result.email(),
                EstadoMembresia.valueOf(result.membresia().name()),
                result.fechaAlta().atOffset(ZoneOffset.UTC));
    }
}
