package com.cantero.sistema_inventario.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApi(ApiException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        pd.setTitle(titulo(ex.getStatus()));
        pd.setType(URI.create("urn:problem:" + ex.getCodigo().toLowerCase()));
        pd.setProperty("codigo", ex.getCodigo());
        return pd;
    }

    private String titulo(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Solicitud inválida";
            case UNAUTHORIZED -> "No autenticado";
            case FORBIDDEN -> "Acceso denegado";
            case NOT_FOUND -> "No encontrado";
            case CONFLICT -> "Conflicto";
            case METHOD_NOT_ALLOWED -> "Método no permitido";
            default -> status.is5xxServerError() ? "Error interno" : "Error";
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "campo", error.getField(),
                        "mensaje", error.getDefaultMessage() == null ? "" : error.getDefaultMessage()))
                .toList();

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Datos de entrada inválidos");
        pd.setTitle("Validación fallida");
        pd.setType(URI.create("urn:problem:validacion"));
        pd.setProperty("codigo", "VALIDACION");
        pd.setProperty("detalles", detalles);
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMensajeIlegible(HttpMessageNotReadableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido o tiene un formato incorrecto");
        pd.setTitle("Petición inválida");
        pd.setType(URI.create("urn:problem:json-invalido"));
        pd.setProperty("codigo", "JSON_INVALIDO");
        return pd;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "El parámetro '" + ex.getName() + "' tiene un valor inválido");
        pd.setTitle("Parámetro inválido");
        pd.setType(URI.create("urn:problem:parametro-invalido"));
        pd.setProperty("codigo", "PARAMETRO_INVALIDO");
        return pd;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMetodoNoPermitido(HttpRequestMethodNotSupportedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.METHOD_NOT_ALLOWED, "El método HTTP no está permitido para esta ruta");
        pd.setTitle("Método no permitido");
        pd.setType(URI.create("urn:problem:metodo-no-permitido"));
        pd.setProperty("codigo", "METODO_NO_PERMITIDO");
        return pd;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleRutaNoEncontrada(NoResourceFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, "La ruta solicitada no existe");
        pd.setTitle("Ruta no encontrada");
        pd.setType(URI.create("urn:problem:ruta-no-encontrada"));
        pd.setProperty("codigo", "RUTA_NO_ENCONTRADA");
        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleConflict(DataIntegrityViolationException ex) {
        log.warn("Conflicto de integridad de datos", ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "El recurso entra en conflicto con datos existentes");
        pd.setTitle("Conflicto");
        pd.setType(URI.create("urn:problem:conflicto"));
        pd.setProperty("codigo", "CONFLICTO");
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleInesperado(Exception ex) {
        log.error("Error no controlado", ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Se produjo un error interno; inténtalo de nuevo más tarde");
        pd.setTitle("Error interno");
        pd.setType(URI.create("urn:problem:error-interno"));
        pd.setProperty("codigo", "ERROR_INTERNO");
        return pd;
    }
}
