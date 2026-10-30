package com.barbeariaoliveira.exception;

public class RecaptchaInvalidoException extends RuntimeException {

    public RecaptchaInvalidoException(String mensagem) {
        super(mensagem);
    }
}
