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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DniDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.EmailDuplicadoException;
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

        @GetMapping("/test/unexpected")
        void unexpected() {
            throw new IllegalStateException(INTERNAL_DETAIL);
        }
    }
}
