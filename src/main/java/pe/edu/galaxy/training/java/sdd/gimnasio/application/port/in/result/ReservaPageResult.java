package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result;

import java.util.List;

/**
 * Reservas de un socio (US05).
 */
public record ReservaPageResult(List<ReservaResult> items, long totalElements) {

    public ReservaPageResult {
        items = List.copyOf(items);
    }
}
