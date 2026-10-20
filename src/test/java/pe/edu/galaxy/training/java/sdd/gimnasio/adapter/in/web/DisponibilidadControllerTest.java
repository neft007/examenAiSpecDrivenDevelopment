package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarDisponibilidadQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.DisponibilidadResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseNotFoundException;

@WebMvcTest(ReservaController.class)
@Import({ReservaWebMapper.class, GlobalExceptionHandler.class})
class DisponibilidadControllerTest extends ReservaControllerTestSupport {

    private static final String DISPONIBILIDAD = "/api/v1/clases/" + CLASE_ID + "/disponibilidad";

    @Test
    void US05_CA03_returnsAvailablePlaces() throws Exception {
        when(consultarDisponibilidadUseCase.consultar(new ConsultarDisponibilidadQuery(CLASE_ID)))
                .thenReturn(new DisponibilidadResult(CLASE_ID, 20, 2, 18));

        mockMvc.perform(get(DISPONIBILIDAD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claseId").value(CLASE_ID.toString()))
                .andExpect(jsonPath("$.aforoMaximo").value(20))
                .andExpect(jsonPath("$.reservasConfirmadas").value(2))
                .andExpect(jsonPath("$.plazasDisponibles").value(18));
    }

    @Test
    void US05_CA04_rejectsUnknownClase() throws Exception {
        when(consultarDisponibilidadUseCase.consultar(any()))
                .thenThrow(new ClaseNotFoundException(CLASE_ID.toString()));

        mockMvc.perform(get(DISPONIBILIDAD))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CLASE_NOT_FOUND"));
    }
}
