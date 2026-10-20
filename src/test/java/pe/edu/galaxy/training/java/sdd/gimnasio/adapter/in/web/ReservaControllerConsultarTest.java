package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarReservasQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaPageResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.SocioNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;

@WebMvcTest(ReservaController.class)
@Import({ReservaWebMapper.class, GlobalExceptionHandler.class})
class ReservaControllerConsultarTest extends ReservaControllerTestSupport {

    private static final String RESERVAS = "/api/v1/socios/" + SOCIO_ID + "/reservas";
    private static final UUID OTRA_RESERVA_ID = UUID.fromString("1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d");

    @Test
    void US05_CA01_returnsReservasOfTheSocio() throws Exception {
        when(consultarReservasUseCase.consultar(new ConsultarReservasQuery(SOCIO_ID, null)))
                .thenReturn(new ReservaPageResult(List.of(
                        reserva(RESERVA_ID, EstadoReserva.CONFIRMADA),
                        reserva(OTRA_RESERVA_ID, EstadoReserva.CANCELADA)), 2));

        mockMvc.perform(get(RESERVAS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].socioId").value(SOCIO_ID.toString()))
                .andExpect(jsonPath("$.items[1].socioId").value(SOCIO_ID.toString()));
    }

    @Test
    void US05_CA02_filtersByEstado() throws Exception {
        when(consultarReservasUseCase.consultar(new ConsultarReservasQuery(SOCIO_ID, EstadoReserva.CONFIRMADA)))
                .thenReturn(new ReservaPageResult(List.of(reserva(RESERVA_ID, EstadoReserva.CONFIRMADA)), 1));

        mockMvc.perform(get(RESERVAS).param("estado", "CONFIRMADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].estado").value("CONFIRMADA"));
    }

    @Test
    void US05_CA02_rejectsEstadoOutOfCatalog() throws Exception {
        mockMvc.perform(get(RESERVAS).param("estado", "PENDIENTE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value("estado: invalid value"));
        verifyNoInteractions(consultarReservasUseCase);
    }

    @Test
    void rejectsUnknownSocio() throws Exception {
        when(consultarReservasUseCase.consultar(any())).thenThrow(new SocioNotFoundException(SOCIO_ID.toString()));

        mockMvc.perform(get(RESERVAS))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SOCIO_NOT_FOUND"));
    }
}
