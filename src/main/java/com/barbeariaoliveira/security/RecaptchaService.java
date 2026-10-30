package com.barbeariaoliveira.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.barbeariaoliveira.config.RecaptchaProperties;
import com.barbeariaoliveira.exception.RecaptchaInvalidoException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Service
public class RecaptchaService {

    private static final Logger log = LoggerFactory.getLogger(RecaptchaService.class);
    private static final String MSG_INVALIDO = "Não foi possível confirmar que você não é um robô. Recarregue a página e tente novamente.";

    private final RecaptchaProperties properties;
    private final RestClient restClient;

    public RecaptchaService(RecaptchaProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.create();
    }

    public void verificar(String token, String acaoEsperada) {
        if (!properties.habilitado()) {
            return; // interruptor desligado (ambiente de desenvolvimento)
        }

        if (token == null || token.isBlank()) {
            throw new RecaptchaInvalidoException(MSG_INVALIDO);
        }

        RespostaGoogle resposta;
        try {
            resposta = consultarGoogle(token);
        } catch (RestClientException e) {
            // Falhar ABERTO: o Google está fora do ar, mas o rate limiting continua
            // protegendo
            log.warn("Falha ao consultar o reCAPTCHA; requisição liberada. Motivo: {}", e.getMessage());
            return;
        }

        if (resposta == null) {
            log.warn("Resposta vazia do reCAPTCHA; requisição liberada.");
            return;
        }

        boolean valido = resposta.success()
                && resposta.score() >= properties.notaMinima()
                && acaoEsperada.equals(resposta.action());

        if (!valido) {
            throw new RecaptchaInvalidoException(MSG_INVALIDO);
        }
    }

    private RespostaGoogle consultarGoogle(String token) {
        MultiValueMap<String, String> formulario = new LinkedMultiValueMap<>();
        formulario.add("secret", properties.segredo());
        formulario.add("response", token);

        return restClient.post()
                .uri(properties.url())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formulario)
                .retrieve()
                .body(RespostaGoogle.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record RespostaGoogle(
            boolean success,
            double score,
            String action) {
    }
}
