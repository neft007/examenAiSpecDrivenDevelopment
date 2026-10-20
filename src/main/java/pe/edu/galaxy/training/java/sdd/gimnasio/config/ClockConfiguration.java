package pe.edu.galaxy.training.java.sdd.gimnasio.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root: reloj del sistema para los servicios (architecture.md §2).
 */
@Configuration
public class ClockConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
