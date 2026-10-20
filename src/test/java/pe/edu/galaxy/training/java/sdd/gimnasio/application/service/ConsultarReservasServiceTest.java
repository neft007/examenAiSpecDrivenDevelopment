package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarReservasQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaPageResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.SocioNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Dni;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Email;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

class ConsultarReservasServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    private InMemorySocioPersistence socios;
    private InMemoryReservaPersistence reservas;
    private ConsultarReservasService service;

    private Socio ana;
    private Reserva confirmada;
    private Reserva cancelada;

    @BeforeEach
    void setUp() {
        socios = new InMemorySocioPersistence();
        reservas = new InMemoryReservaPersistence(new InMemoryClasePersistence());
        service = new ConsultarReservasService(socios, reservas);

        ana = socios.save(Socio.registrar(new Dni("12345678"), "Ana Pérez", new Email("ana@gym.pe"), CLOCK));
        Socio luis = socios.save(Socio.registrar(new Dni("87654321"), "Luis Díaz", new Email("luis@gym.pe"), CLOCK));
        confirmada = reservas.save(Reserva.crear(ana.getId(), ClaseId.newId(), CLOCK));
        cancelada = reservas.save(Reserva.restore(ReservaId.newId(), ana.getId(), ClaseId.newId(),
                NOW.minus(Duration.ofDays(1)), EstadoReserva.CANCELADA));
        reservas.save(Reserva.crear(luis.getId(), ClaseId.newId(), CLOCK));
    }

    @Test
    void US05_CA01_returnsOnlyTheReservasOfTheSocio() {
        ReservaPageResult result = service.consultar(new ConsultarReservasQuery(ana.getId().value(), null));

        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.items()).extracting(ReservaResult::id)
                .containsExactlyInAnyOrder(confirmada.getId().value(), cancelada.getId().value());
        assertThat(result.items()).allMatch(r -> r.socioId().equals(ana.getId().value()));
    }

    @Test
    void US05_CA01_returnsEmptyPageForSocioWithoutReservas() {
        Socio sinReservas = socios.save(Socio.registrar(new Dni("11223344"), "Eva Soto", new Email("eva@gym.pe"),
                CLOCK));

        ReservaPageResult result = service.consultar(new ConsultarReservasQuery(sinReservas.getId().value(), null));

        assertThat(result.items()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    void US05_CA02_filtersByConfirmadaState() {
        ReservaPageResult result = service.consultar(
                new ConsultarReservasQuery(ana.getId().value(), EstadoReserva.CONFIRMADA));

        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.items()).extracting(ReservaResult::id).containsExactly(confirmada.getId().value());
        assertThat(result.items()).allMatch(r -> r.estado() == EstadoReserva.CONFIRMADA);
    }

    @Test
    void US05_CA02_filtersByCanceladaState() {
        ReservaPageResult result = service.consultar(
                new ConsultarReservasQuery(ana.getId().value(), EstadoReserva.CANCELADA));

        assertThat(result.items()).extracting(ReservaResult::id).containsExactly(cancelada.getId().value());
    }

    @Test
    void rejectsUnknownSocio() {
        assertThatThrownBy(() -> service.consultar(new ConsultarReservasQuery(UUID.randomUUID(), null)))
                .isInstanceOf(SocioNotFoundException.class);
    }
}
