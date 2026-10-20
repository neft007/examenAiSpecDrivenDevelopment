package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.AforoCompletoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.CancelacionFueraDePlazoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseYaComenzadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DniDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.EmailDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.InvalidStateTransitionException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.MembresiaVencidaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaSolapadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.SocioNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;

/**
 * Cada fila de ADR-0004 con excepción ya implementada, e INTERNAL_ERROR sin detalles internos.
 */
@WebMvcTest(GlobalExceptionHandlerTest.ThrowingController.class)
@Import({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.ThrowingController.class})
class GlobalExceptionHandlerTest {

    private static final String INTERNAL_DETAIL = "db password=secret at SocioJpaEntity";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validationException_returns400ValidationError() throws Exception {
        mockMvc.perform(get("/test/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("dni: must have exactly 8 digits (e.g. 12345678)"));
    }

    @Test
    void methodArgumentNotValid_returns400ValidationError() throws Exception {
        mockMvc.perform(post("/test/body").contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value(Matchers.startsWith("value: ")));
    }

    @Test
    void malformedJson_returns400ValidationError() throws Exception {
        mockMvc.perform(post("/test/body").contentType(MediaType.APPLICATION_JSON).content("{\"value\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value("body: missing or malformed JSON"));
    }

    @Test
    void missingBody_returns400ValidationError() throws Exception {
        mockMvc.perform(post("/test/body").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void dniDuplicado_returns409DniDuplicado() throws Exception {
        mockMvc.perform(get("/test/dni-duplicado"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DNI_DUPLICADO"))
                .andExpect(jsonPath("$.details", not(empty())));
    }

    @Test
    void emailDuplicado_returns409EmailDuplicado() throws Exception {
        mockMvc.perform(get("/test/email-duplicado"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_DUPLICADO"))
                .andExpect(jsonPath("$.details", not(empty())));
    }

    @ParameterizedTest
    @CsvSource({
            "SOCIO_NOT_FOUND, 404",
            "CLASE_NOT_FOUND, 404",
            "RESERVA_NOT_FOUND, 404",
            "MEMBRESIA_VENCIDA, 409",
            "CLASE_YA_COMENZADA, 409",
            "AFORO_COMPLETO, 409",
            "RESERVA_SOLAPADA, 409",
            "INVALID_STATE_TRANSITION, 409",
            "CANCELACION_FUERA_DE_PLAZO, 409"
    })
    void domainException_returnsStatusAndCodeOfAdr0004(String code, int httpStatus) throws Exception {
        mockMvc.perform(get("/test/domain/" + code))
                .andExpect(status().is(httpStatus))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.details", not(empty())));
    }

    @Test
    void unexpectedException_returns500InternalErrorWithoutInternalDetails() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.details", empty()))
                .andExpect(content().string(not(Matchers.containsString("secret"))))
                .andExpect(content().string(not(Matchers.containsString("IllegalStateException"))));
    }

    record TestBody(@NotBlank String value) {
    }

    @RestController
    static class ThrowingController {

        @GetMapping("/test/validation")
        void validation() {
            throw new ValidationException("dni: must have exactly 8 digits (e.g. 12345678)");
        }

        @PostMapping("/test/body")
        void body(@Valid @RequestBody TestBody body) {
        }

        @GetMapping("/test/dni-duplicado")
        void dniDuplicado() {
            throw new DniDuplicadoException("12345678");
        }

        @GetMapping("/test/email-duplicado")
        void emailDuplicado() {
            throw new EmailDuplicadoException("ana@gym.pe");
        }

        @GetMapping("/test/domain/{code}")
        void domain(@PathVariable String code) {
            throw switch (code) {
                case "SOCIO_NOT_FOUND" -> new SocioNotFoundException("3f2b8c1e-4d5a-4b6c-9e7f-1a2b3c4d5e6f");
                case "CLASE_NOT_FOUND" -> new ClaseNotFoundException("7a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d");
                case "RESERVA_NOT_FOUND" -> new ReservaNotFoundException("9c8b7a6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d");
                case "MEMBRESIA_VENCIDA" -> new MembresiaVencidaException("3f2b8c1e-4d5a-4b6c-9e7f-1a2b3c4d5e6f");
                case "CLASE_YA_COMENZADA" -> new ClaseYaComenzadaException("7a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d");
                case "AFORO_COMPLETO" -> new AforoCompletoException("7a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d");
                case "RESERVA_SOLAPADA" -> new ReservaSolapadaException("3f2b8c1e-4d5a-4b6c-9e7f-1a2b3c4d5e6f",
                        "2026-10-20T18:00:00Z");
                case "INVALID_STATE_TRANSITION" -> new InvalidStateTransitionException("CANCELADA", "CANCELADA");
                case "CANCELACION_FUERA_DE_PLAZO" ->
                        new CancelacionFueraDePlazoException("9c8b7a6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d");
                default -> new IllegalArgumentException(code);
            };
        }

        @GetMapping("/test/unexpected")
        void unexpected() {
            throw new IllegalStateException(INTERNAL_DETAIL);
        }
    }
}
