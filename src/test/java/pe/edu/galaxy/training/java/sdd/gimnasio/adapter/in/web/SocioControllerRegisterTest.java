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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.RegistrarSocioCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.SocioResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.RegistrarSocioUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DniDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.EmailDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Membresia;

@WebMvcTest(SocioController.class)
@Import({SocioWebMapper.class, GlobalExceptionHandler.class})
class SocioControllerRegisterTest {

    private static final UUID ID = UUID.fromString("3f2b8c1e-4d5a-4b6c-9e7f-1a2b3c4d5e6f");
    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarSocioUseCase registrarSocioUseCase;

    @Test
    void US01_CA01_registersSocioWithActiveMembership() throws Exception {
        when(registrarSocioUseCase.registrar(new RegistrarSocioCommand("12345678", "Ana Pérez", "ana@gym.pe")))
                .thenReturn(new SocioResult(ID, "12345678", "Ana Pérez", "ana@gym.pe", Membresia.ACTIVA, NOW));

        register("12345678", "Ana Pérez", "ana@gym.pe")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/socios/" + ID))
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.dni").value("12345678"))
                .andExpect(jsonPath("$.nombre").value("Ana Pérez"))
                .andExpect(jsonPath("$.email").value("ana@gym.pe"))
                .andExpect(jsonPath("$.membresia").value("ACTIVA"));
    }

    @Test
    void US01_CA02_rejectsDuplicatedDni() throws Exception {
        when(registrarSocioUseCase.registrar(any())).thenThrow(new DniDuplicadoException("12345678"));

        register("12345678", "Ana Pérez", "ana@gym.pe")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DNI_DUPLICADO"));
    }

    @Test
    void US01_CA03_rejectsDniWithInvalidFormat() throws Exception {
        register("1234A678", "Ana Pérez", "ana@gym.pe")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(registrarSocioUseCase);
    }

    @Test
    void US01_CA04_rejectsDuplicatedEmail() throws Exception {
        when(registrarSocioUseCase.registrar(any())).thenThrow(new EmailDuplicadoException("ana@gym.pe"));

        register("12345678", "Ana Pérez", "ana@gym.pe")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_DUPLICADO"));
    }

    @Test
    void US01_CA05_rejectsEmailWithInvalidFormat() throws Exception {
        register("12345678", "Ana Pérez", "ana.gym.pe")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(registrarSocioUseCase);
    }

    @Test
    void US01_CA06_rejectsNombreWithLeadingOrTrailingSpaces() throws Exception {
        register("12345678", " Ana Pérez", "ana@gym.pe")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        register("12345678", "Ana Pérez ", "ana@gym.pe")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(registrarSocioUseCase);
    }

    private ResultActions register(String dni, String nombre, String email) throws Exception {
        String body = """
                {"dni": "%s", "nombre": "%s", "email": "%s"}
                """.formatted(dni, nombre, email);
        return mockMvc.perform(post("/api/v1/socios").contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
