package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CancelarReservaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.CancelacionFueraDePlazoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.InvalidStateTransitionException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;

@WebMvcTest(ReservaController.class)
@Import({ReservaWebMapper.class, GlobalExceptionHandler.class})
class ReservaControllerCancelarTest extends ReservaControllerTestSupport {

    private static final String CANCELAR = "/api/v1/reservas/" + RESERVA_ID + "/cancelar";

    @Test
    void US04_CA01_cancelsReserva() throws Exception {
        when(cancelarReservaUseCase.cancelar(new CancelarReservaCommand(RESERVA_ID)))
                .thenReturn(reserva(RESERVA_ID, EstadoReserva.CANCELADA));

        mockMvc.perform(post(CANCELAR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RESERVA_ID.toString()))
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void US04_CA02_rejectsCancellationWithLessThanTwoHoursInAdvance() throws Exception {
        when(cancelarReservaUseCase.cancelar(any()))
                .thenThrow(new CancelacionFueraDePlazoException(RESERVA_ID.toString()));

        mockMvc.perform(post(CANCELAR))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CANCELACION_FUERA_DE_PLAZO"));
    }

    @Test
    void US04_CA03_rejectsCancellationOfAlreadyCancelledReserva() throws Exception {
        when(cancelarReservaUseCase.cancelar(any()))
                .thenThrow(new InvalidStateTransitionException("CANCELADA", "CANCELADA"));

        mockMvc.perform(post(CANCELAR))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_STATE_TRANSITION"));
    }

    @Test
    void rejectsUnknownReserva() throws Exception {
        when(cancelarReservaUseCase.cancelar(any())).thenThrow(new ReservaNotFoundException(RESERVA_ID.toString()));

        mockMvc.perform(post(CANCELAR))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESERVA_NOT_FOUND"));
    }
}
