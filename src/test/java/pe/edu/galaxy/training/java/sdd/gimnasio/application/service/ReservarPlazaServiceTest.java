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

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.ReservarPlazaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.AforoCompletoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseYaComenzadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.MembresiaVencidaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaSolapadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.SocioNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Dni;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Email;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoClase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Membresia;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

class ReservarPlazaServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final Instant TOMORROW = NOW.plus(Duration.ofDays(1));

    private InMemorySocioPersistence socios;
    private InMemoryClasePersistence clases;
    private InMemoryReservaPersistence reservas;
    private ReservarPlazaService service;

    @BeforeEach
    void setUp() {
        socios = new InMemorySocioPersistence();
        clases = new InMemoryClasePersistence();
        reservas = new InMemoryReservaPersistence(clases);
        service = new ReservarPlazaService(socios, clases, reservas, CLOCK);
    }

    @Test
    void US03_CA01_createsConfirmadaReserva() {
        Socio socio = socio(Membresia.ACTIVA);
        Clase clase = clase(TOMORROW, 20);

        ReservaResult result = service.reservar(command(socio, clase));

        assertThat(result.id()).isNotNull();
        assertThat(result.socioId()).isEqualTo(socio.getId().value());
        assertThat(result.claseId()).isEqualTo(clase.getId().value());
        assertThat(result.fechaReserva()).isEqualTo(NOW);
        assertThat(result.estado()).isEqualTo(EstadoReserva.CONFIRMADA);
        assertThat(reservas.findById(ReservaId.of(result.id()))).isPresent();
    }

    @Test
    void US03_CA02_rejectsSocioWithExpiredMembership() {
        // VENCIDA no tiene flujo de API en V1: el socio se crea con restore (lifecycle.md, traceability.md)
        Socio socio = socio(Membresia.VENCIDA);
        Clase clase = clase(TOMORROW, 20);

        assertRejectedAndNotSaved(command(socio, clase), MembresiaVencidaException.class);
    }

    @Test
    void US03_CA03_rejectsWhenClaseIsFull() {
        Clase clase = clase(TOMORROW, 2);
        reservas.save(Reserva.crear(socio(Membresia.ACTIVA).getId(), clase.getId(), CLOCK));
        reservas.save(Reserva.crear(socio(Membresia.ACTIVA).getId(), clase.getId(), CLOCK));

        assertThatThrownBy(() -> service.reservar(command(socio(Membresia.ACTIVA), clase)))
                .isInstanceOf(AforoCompletoException.class);
        assertThat(reservas.count()).isEqualTo(2);
    }

    @Test
    void US03_CA03_cancelledReservasDoNotTakePlaces() {
        Clase clase = clase(TOMORROW, 1);
        reservas.save(Reserva.restore(ReservaId.newId(), socio(Membresia.ACTIVA).getId(), clase.getId(), NOW,
                EstadoReserva.CANCELADA));

        assertThat(service.reservar(command(socio(Membresia.ACTIVA), clase)).estado())
                .isEqualTo(EstadoReserva.CONFIRMADA);
    }

    @Test
    void US03_CA04_rejectsOverlappingReservaOfSameSocio() {
        Socio socio = socio(Membresia.ACTIVA);
        Clase spinning = clase(TOMORROW, 20);
        Clase yogaMismoHorario = clase(TOMORROW, 20);
        service.reservar(command(socio, spinning));

        assertThatThrownBy(() -> service.reservar(command(socio, yogaMismoHorario)))
                .isInstanceOf(ReservaSolapadaException.class);
        assertThat(reservas.count()).isEqualTo(1);
    }

    @Test
    void US03_CA05_rejectsClaseThatAlreadyStarted() {
        // Una clase pasada solo existe vía restore: Clase.crear exige fecha futura (INV-06)
        Clase empezada = Clase.restore(ClaseId.newId(), "Spinning", "Carla Ruiz", NOW.minusSeconds(60), 60, 20,
                EstadoClase.PROGRAMADA);
        clases.save(empezada);

        assertRejectedAndNotSaved(command(socio(Membresia.ACTIVA), empezada), ClaseYaComenzadaException.class);
    }

    @Test
    void US03_CA05_rejectsClaseStartingRightNow() {
        Clase ahora = Clase.restore(ClaseId.newId(), "Spinning", "Carla Ruiz", NOW, 60, 20, EstadoClase.PROGRAMADA);
        clases.save(ahora);

        assertRejectedAndNotSaved(command(socio(Membresia.ACTIVA), ahora), ClaseYaComenzadaException.class);
    }

    @Test
    void rejectsUnknownSocio() {
        Clase clase = clase(TOMORROW, 20);

        assertRejectedAndNotSaved(new ReservarPlazaCommand(UUID.randomUUID(), clase.getId().value()),
                SocioNotFoundException.class);
    }

    @Test
    void rejectsUnknownClase() {
        Socio socio = socio(Membresia.ACTIVA);

        assertRejectedAndNotSaved(new ReservarPlazaCommand(socio.getId().value(), UUID.randomUUID()),
                ClaseNotFoundException.class);
    }

    private Socio socio(Membresia membresia) {
        String dni = String.format("%08d", socios.count() + 1);
        return socios.save(Socio.restore(SocioId.newId(), new Dni(dni), "Ana Pérez",
                new Email("socio" + dni + "@gym.pe"), membresia, NOW));
    }

    private Clase clase(Instant fechaInicio, int aforo) {
        return clases.save(Clase.crear("Spinning", "Carla Ruiz", fechaInicio, 60, aforo, CLOCK));
    }

    private static ReservarPlazaCommand command(Socio socio, Clase clase) {
        return new ReservarPlazaCommand(socio.getId().value(), clase.getId().value());
    }

    private void assertRejectedAndNotSaved(ReservarPlazaCommand command, Class<? extends Throwable> expected) {
        assertThatThrownBy(() -> service.reservar(command)).isInstanceOf(expected);
        assertThat(reservas.count()).isZero();
    }
}
