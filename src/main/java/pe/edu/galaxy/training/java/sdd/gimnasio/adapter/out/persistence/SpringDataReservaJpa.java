package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;

/**
 * Repositorio Spring Data de reservas. Solo lo usa ReservaPersistenceAdapter.
 */
interface SpringDataReservaJpa extends JpaRepository<ReservaJpaEntity, UUID> {

    long countByClaseIdAndEstado(UUID claseId, EstadoReserva estado);

    // La fecha de inicio pertenece a la clase, por eso se une con clases
    @Query("""
            select count(r) > 0
            from ReservaJpaEntity r join ClaseJpaEntity c on c.id = r.claseId
            where r.socioId = :socioId and r.estado = :estado and c.fechaInicio = :fechaInicio
            """)
    boolean existsBySocioIdAndEstadoAndClaseFechaInicio(@Param("socioId") UUID socioId,
                                                        @Param("estado") EstadoReserva estado,
                                                        @Param("fechaInicio") Instant fechaInicio);

    List<ReservaJpaEntity> findBySocioId(UUID socioId);
}
