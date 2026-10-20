package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;

/**
 * Tabla reservas. Socio y clase se referencian por id, igual que en el agregado Reserva.
 */
@Entity
@Table(name = "reservas")
public class ReservaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "socio_id", nullable = false)
    private UUID socioId;

    @Column(name = "clase_id", nullable = false)
    private UUID claseId;

    @Column(name = "fecha_reserva", nullable = false)
    private Instant fechaReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    protected ReservaJpaEntity() {
    }

    ReservaJpaEntity(UUID id, UUID socioId, UUID claseId, Instant fechaReserva, EstadoReserva estado) {
        this.id = id;
        this.socioId = socioId;
        this.claseId = claseId;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }

    UUID getId() {
        return id;
    }

    UUID getSocioId() {
        return socioId;
    }

    UUID getClaseId() {
        return claseId;
    }

    Instant getFechaReserva() {
        return fechaReserva;
    }

    EstadoReserva getEstado() {
        return estado;
    }
}
