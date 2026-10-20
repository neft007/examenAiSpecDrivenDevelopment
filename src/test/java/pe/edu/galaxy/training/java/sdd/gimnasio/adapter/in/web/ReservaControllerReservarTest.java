package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.ReservarPlazaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.AforoCompletoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseYaComenzadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.MembresiaVencidaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaSolapadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;

@WebMvcTest(ReservaController.class)
@Import({ReservaWebMapper.class, GlobalExceptionHandler.class})
class ReservaControllerReservarTest extends ReservaControllerTestSupport {

    @Test
    void US03_CA01_createsConfirmadaReserva() throws Exception {
        when(reservarPlazaUseCase.reservar(new ReservarPlazaCommand(SOCIO_ID, CLASE_ID)))
                .thenReturn(reserva(RESERVA_ID, EstadoReserva.CONFIRMADA));

        reservar(CLASE_ID, SOCIO_ID)
                .andExpect(status().isCreated())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.id").value(RESERVA_ID.toString()))
                .andExpect(jsonPath("$.socioId").value(SOCIO_ID.toString()))
                .andExpect(jsonPath("$.claseId").value(CLASE_ID.toString()))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void US03_CA02_rejectsSocioWithExpiredMembership() throws Exception {
        when(reservarPlazaUseCase.reservar(any())).thenThrow(new MembresiaVencidaException(SOCIO_ID.toString()));

        reservar(CLASE_ID, SOCIO_ID)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("MEMBRESIA_VENCIDA"));
    }

    @Test
    void US03_CA03_rejectsWhenClaseIsFull() throws Exception {
        when(reservarPlazaUseCase.reservar(any())).thenThrow(new AforoCompletoException(CLASE_ID.toString()));

        reservar(CLASE_ID, SOCIO_ID)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("AFORO_COMPLETO"));
    }

    @Test
    void US03_CA04_rejectsOverlappingReserva() throws Exception {
        when(reservarPlazaUseCase.reservar(any()))
                .thenThrow(new ReservaSolapadaException(SOCIO_ID.toString(), "2026-10-20T18:00:00Z"));

        reservar(CLASE_ID, SOCIO_ID)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESERVA_SOLAPADA"));
    }

    @Test
    void US03_CA05_rejectsClaseThatAlreadyStarted() throws Exception {
        when(reservarPlazaUseCase.reservar(any())).thenThrow(new ClaseYaComenzadaException(CLASE_ID.toString()));

        reservar(CLASE_ID, SOCIO_ID)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CLASE_YA_COMENZADA"));
    }

    @Test
    void rejectsUnknownClase() throws Exception {
        when(reservarPlazaUseCase.reservar(any())).thenThrow(new ClaseNotFoundException(CLASE_ID.toString()));

        reservar(CLASE_ID, SOCIO_ID)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CLASE_NOT_FOUND"));
    }

    @Test
    void rejectsMissingSocioId() throws Exception {
        mockMvc.perform(post("/api/v1/clases/" + CLASE_ID + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(reservarPlazaUseCase);
    }

    @Test
    void rejectsMalformedClaseId() throws Exception {
        mockMvc.perform(post("/api/v1/clases/not-a-uuid/reservas")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"socioId\": \"" + SOCIO_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value("claseId: invalid value"));
        verifyNoInteractions(reservarPlazaUseCase);
    }

    private ResultActions reservar(UUID claseId, UUID socioId) throws Exception {
        return mockMvc.perform(post("/api/v1/clases/" + claseId + "/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"socioId\": \"" + socioId + "\"}"));
    }
}
