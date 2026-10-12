package com.barbeariaoliveira.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.barbeariaoliveira.dto.ErroResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse tratarRegraNegocio(RegraNegocioException ex) {
        return new ErroResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                Instant.now(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse tratarValidacao(MethodArgumentNotValidException ex) {
    Map<String, String> campos = new HashMap<>();

    for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
        campos.put(erro.getField(), erro.getDefaultMessage()); 
    }

    return new ErroResponse(
        HttpStatus.BAD_REQUEST.value(),
        "Dados Inválidos.",
        Instant.now(),
        campos
    );
}

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse tratarNaoEncontrado(RecursoNaoEncontradoException ex){
        return new ErroResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            Instant.now(),
            null
        ); 
    }
}