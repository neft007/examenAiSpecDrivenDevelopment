package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.ClasesApi;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ClaseResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.CrearClaseRequest;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.CrearClaseUseCase;

/**
 * Operaciones del tag Clases del contrato (US02).
 */
@RestController
public class ClaseController implements ClasesApi {

    private final CrearClaseUseCase crearClaseUseCase;
    private final ClaseWebMapper mapper;

    public ClaseController(CrearClaseUseCase crearClaseUseCase, ClaseWebMapper mapper) {
        this.crearClaseUseCase = crearClaseUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<ClaseResponse> crearClase(CrearClaseRequest crearClaseRequest) {
        ClaseResponse response = mapper.toResponse(crearClaseUseCase.crear(mapper.toCommand(crearClaseRequest)));
        URI location = URI.create(PATH_CREAR_CLASE + "/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }
}
