package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CrearClaseCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ClaseResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.CrearClaseUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoClase;

@WebMvcTest(ClaseController.class)
@Import({ClaseWebMapper.class, GlobalExceptionHandler.class})
class ClaseControllerCreateTest {

    private static final UUID ID = UUID.fromString("7a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d");
    private static final String FECHA = "2026-10-20T18:00:00Z";
    private static final Instant FECHA_INICIO = Instant.parse(FECHA);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearClaseUseCase crearClaseUseCase;

    @Test
    void US02_CA01_createsClaseInProgramadaState() throws Exception {
        when(crearClaseUseCase.crear(new CrearClaseCommand("Spinning", "Carla Ruiz", FECHA_INICIO, 60, 20)))
                .thenReturn(new ClaseResult(ID, "Spinning", "Carla Ruiz", FECHA_INICIO, 60, 20,
                        EstadoClase.PROGRAMADA));

        crear("Spinning", "Carla Ruiz", FECHA, 60, 20)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/clases/" + ID))
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Spinning"))
                .andExpect(jsonPath("$.instructor").value("Carla Ruiz"))
                .andExpect(jsonPath("$.duracionMinutos").value(60))
                .andExpect(jsonPath("$.aforoMaximo").value(20))
                .andExpect(jsonPath("$.estado").value("PROGRAMADA"));
    }

    @Test
    void US02_CA02_rejectsStartDateInThePast() throws Exception {
        // El contrato no puede expresar "fecha futura": la regla la aplica el dominio (INV-06)
        when(crearClaseUseCase.crear(any()))
                .thenThrow(new ValidationException("fechaInicio: must be in the future"));

        crear("Spinning", "Carla Ruiz", "2020-01-01T10:00:00Z", 60, 20)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value("fechaInicio: must be in the future"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 51})
    void US02_CA03_rejectsAforoOutOfRange(int aforo) throws Exception {
        crear("Spinning", "Carla Ruiz", FECHA, 60, aforo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(crearClaseUseCase);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 181})
    void US02_CA04_rejectsDuracionOutOfRange(int duracion) throws Exception {
        crear("Spinning", "Carla Ruiz", FECHA, duracion, 20)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(crearClaseUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {" Spinning", "Spinning "})
    void US02_CA05_rejectsNombreWithLeadingOrTrailingSpaces(String nombre) throws Exception {
        crear(nombre, "Carla Ruiz", FECHA, 60, 20)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(crearClaseUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {" Carla Ruiz", "Carla Ruiz "})
    void US02_CA06_rejectsInstructorWithLeadingOrTrailingSpaces(String instructor) throws Exception {
        crear("Spinning", instructor, FECHA, 60, 20)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(crearClaseUseCase);
    }

    private ResultActions crear(String nombre, String instructor, String fechaInicio, int duracion, int aforo)
            throws Exception {
        String body = """
                {"nombre": "%s", "instructor": "%s", "fechaInicio": "%s", "duracionMinutos": %d, "aforoMaximo": %d}
                """.formatted(nombre, instructor, fechaInicio, duracion, aforo);
        return mockMvc.perform(post("/api/v1/clases").contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
