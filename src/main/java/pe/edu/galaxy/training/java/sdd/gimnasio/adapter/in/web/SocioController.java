package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.SociosApi;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.RegistrarSocioRequest;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.SocioResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.RegistrarSocioUseCase;

/**
 * Operaciones del tag Socios del contrato (US01).
 */
@RestController
public class SocioController implements SociosApi {

    private final RegistrarSocioUseCase registrarSocioUseCase;
    private final SocioWebMapper mapper;

    public SocioController(RegistrarSocioUseCase registrarSocioUseCase, SocioWebMapper mapper) {
        this.registrarSocioUseCase = registrarSocioUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<SocioResponse> registrarSocio(RegistrarSocioRequest registrarSocioRequest) {
        SocioResponse response = mapper.toResponse(
                registrarSocioUseCase.registrar(mapper.toCommand(registrarSocioRequest)));
        URI location = URI.create(PATH_REGISTRAR_SOCIO + "/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }
}
