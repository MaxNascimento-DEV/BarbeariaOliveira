package com.barbeariaoliveira.notificacao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.barbeariaoliveira.config.TelegramProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Chamadas HTTP à Bot API do Telegram (https://core.telegram.org/bots/api).
 * Nunca lança exceção: falha no Telegram não pode derrubar o agendamento.
 */
@Component
public class TelegramClient {

    private static final Logger log = LoggerFactory.getLogger(TelegramClient.class);
    private static final String URL_BASE = "https://api.telegram.org/bot";

    private final TelegramProperties properties;
    private final RestClient restClient;

    public TelegramClient(TelegramProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.create();
    }

    /** Envia uma mensagem em HTML simples (<b>, <i>, <code>, <a>). */
    public boolean enviarMensagem(String chatId, String html) {
        if (!properties.ativo() || chatId == null || chatId.isBlank()) {
            return false;
        }
        try {
            restClient.post()
                    .uri(URL_BASE + properties.token() + "/sendMessage")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "chat_id", chatId.strip(),
                            "text", html,
                            "parse_mode", "HTML",
                            "disable_web_page_preview", true))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            log.warn("Telegram: falha ao enviar mensagem para o chat {} — {}", chatId, descrever(e));
            return false;
        }
    }

    /**
     * Busca mensagens novas enviadas ao bot (long polling desligado: timeout 0).
     * Lança RestClientException em caso de falha (quem chama decide como logar).
     */
    public List<Atualizacao> buscarAtualizacoes(long offset) {
        if (!properties.ativo()) {
            return List.of();
        }
        RespostaAtualizacoes resposta = restClient.get()
                .uri(URL_BASE + properties.token() + "/getUpdates?timeout=0&offset=" + offset)
                .retrieve()
                .body(RespostaAtualizacoes.class);
        return resposta == null || resposta.result() == null ? List.of() : resposta.result();
    }

    /** Confere o token: devolve o @usuario do bot. Lança RestClientException se o token for inválido. */
    public String buscarNomeDoBot() {
        RespostaBot resposta = restClient.get()
                .uri(URL_BASE + properties.token() + "/getMe")
                .retrieve()
                .body(RespostaBot.class);
        return resposta == null || resposta.result() == null ? "?" : resposta.result().username();
    }

    /** Remove webhook antigo (se existir, o getUpdates não funciona). */
    public void removerWebhook() {
        restClient.get()
                .uri(URL_BASE + properties.token() + "/deleteWebhook")
                .retrieve()
                .toBodilessEntity();
    }

    /** Texto do erro com o token mascarado (nunca deixar o token aparecer no log). */
    public String descrever(RestClientException e) {
        String texto;
        if (e instanceof RestClientResponseException r) {
            String corpo = r.getResponseBodyAsString();
            texto = "HTTP " + r.getStatusCode().value() + " " + (corpo == null ? "" : corpo.strip());
        } else {
            Throwable causa = e.getMostSpecificCause();
            texto = causa.getClass().getSimpleName() + (causa.getMessage() == null ? "" : ": " + causa.getMessage());
        }
        String token = properties.token();
        return token == null || token.isBlank() ? texto : texto.replace(token, "***");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RespostaBot(boolean ok, Bot result) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Bot(String username) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RespostaAtualizacoes(boolean ok, List<Atualizacao> result) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Atualizacao(@JsonProperty("update_id") long updateId, Mensagem message) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Mensagem(Chat chat, String text) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Chat(long id, @JsonProperty("first_name") String primeiroNome) {
    }
}
