package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ErrorResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.AforoCompletoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.CancelacionFueraDePlazoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseYaComenzadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DniDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DomainException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.EmailDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.InvalidStateTransitionException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.MembresiaVencidaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaSolapadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.SocioNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;

/**
 * Traduce las excepciones a ErrorResponse con el HTTP y el code de ADR-0004.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex);
    }

    // Restricciones del DTO generado (@NotNull, @Pattern, @Size, @Email)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", "Validation failed", details));
    }

    // Cuerpo ausente o JSON mal formado; el mensaje del parser no se expone
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", "Validation failed",
                        List.of("body: missing or malformed JSON")));
    }

    @ExceptionHandler(DniDuplicadoException.class)
    ResponseEntity<ErrorResponse> handleDniDuplicado(DniDuplicadoException ex) {
        return error(HttpStatus.CONFLICT, "DNI_DUPLICADO", ex);
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    ResponseEntity<ErrorResponse> handleEmailDuplicado(EmailDuplicadoException ex) {
        return error(HttpStatus.CONFLICT, "EMAIL_DUPLICADO", ex);
    }

    @ExceptionHandler(SocioNotFoundException.class)
    ResponseEntity<ErrorResponse> handleSocioNotFound(SocioNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "SOCIO_NOT_FOUND", ex);
    }

    @ExceptionHandler(ClaseNotFoundException.class)
    ResponseEntity<ErrorResponse> handleClaseNotFound(ClaseNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "CLASE_NOT_FOUND", ex);
    }

    @ExceptionHandler(ReservaNotFoundException.class)
    ResponseEntity<ErrorResponse> handleReservaNotFound(ReservaNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "RESERVA_NOT_FOUND", ex);
    }

    @ExceptionHandler(MembresiaVencidaException.class)
    ResponseEntity<ErrorResponse> handleMembresiaVencida(MembresiaVencidaException ex) {
        return error(HttpStatus.CONFLICT, "MEMBRESIA_VENCIDA", ex);
    }

    @ExceptionHandler(ClaseYaComenzadaException.class)
    ResponseEntity<ErrorResponse> handleClaseYaComenzada(ClaseYaComenzadaException ex) {
        return error(HttpStatus.CONFLICT, "CLASE_YA_COMENZADA", ex);
    }

    @ExceptionHandler(AforoCompletoException.class)
    ResponseEntity<ErrorResponse> handleAforoCompleto(AforoCompletoException ex) {
        return error(HttpStatus.CONFLICT, "AFORO_COMPLETO", ex);
    }

    @ExceptionHandler(ReservaSolapadaException.class)
    ResponseEntity<ErrorResponse> handleReservaSolapada(ReservaSolapadaException ex) {
        return error(HttpStatus.CONFLICT, "RESERVA_SOLAPADA", ex);
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    ResponseEntity<ErrorResponse> handleInvalidStateTransition(InvalidStateTransitionException ex) {
        return error(HttpStatus.CONFLICT, "INVALID_STATE_TRANSITION", ex);
    }

    @ExceptionHandler(CancelacionFueraDePlazoException.class)
    ResponseEntity<ErrorResponse> handleCancelacionFueraDePlazo(CancelacionFueraDePlazoException ex) {
        return error(HttpStatus.CONFLICT, "CANCELACION_FUERA_DE_PLAZO", ex);
    }

    // Error no mapeado: no se exponen detalles internos al cliente
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Unexpected error", List.of()));
    }

    private static ResponseEntity<ErrorResponse> error(HttpStatus status, String code, DomainException ex) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, ex.getMessage(), ex.getDetails()));
    }
}
