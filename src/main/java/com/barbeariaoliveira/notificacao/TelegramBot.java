package com.barbeariaoliveira.notificacao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClientException;
import org.springframework.stereotype.Component;

import com.barbeariaoliveira.config.TelegramProperties;

/**
 * Responde quem manda mensagem ao bot com o ID do chat dele.
 * É assim que o barbeiro descobre o número que o admin cadastra no painel (Barbeiros → ID do Telegram).
 *
 * Usa getUpdates a cada 5 s (polling), então funciona até rodando em localhost, sem webhook/HTTPS.
 */
@Component
public class TelegramBot {

    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    private final TelegramProperties properties;
    private final TelegramClient telegramClient;
    private long proximoOffset = 0;
    private String ultimoErro = null; // evita repetir o mesmo erro no log a cada 5 s

    public TelegramBot(TelegramProperties properties, TelegramClient telegramClient) {
        this.properties = properties;
        this.telegramClient = telegramClient;
    }

    /** Ao subir a API, diz no console se o Telegram está ligado e se o token é válido. */
    @EventListener(ApplicationReadyEvent.class)
    public void verificarConfiguracao() {
        if (!properties.habilitado()) {
            log.info("Telegram: DESLIGADO (TELEGRAM_HABILITADO não é true).");
            return;
        }
        if (properties.token() == null || properties.token().isBlank()) {
            log.warn("Telegram: TELEGRAM_HABILITADO=true, mas TELEGRAM_TOKEN está vazio.");
            return;
        }
        try {
            String bot = telegramClient.buscarNomeDoBot();
            telegramClient.removerWebhook();
            log.info("Telegram: LIGADO como @{}. Avisos para admins: {}", bot,
                    properties.chatIdsAdmin().isEmpty() ? "nenhum (TELEGRAM_CHAT_IDS vazio)" : properties.chatIdsAdmin());
        } catch (RestClientException e) {
            log.error("Telegram: não foi possível conectar ao bot — {}. Confira o TELEGRAM_TOKEN.", telegramClient.descrever(e));
        }
    }

    @Scheduled(fixedDelay = 5_000, initialDelay = 5_000)
    public void responderMensagens() {
        if (!properties.ativo()) {
            return;
        }
        List<TelegramClient.Atualizacao> atualizacoes;
        try {
            atualizacoes = telegramClient.buscarAtualizacoes(proximoOffset);
            ultimoErro = null;
        } catch (RestClientException e) {
            String erro = telegramClient.descrever(e);
            if (!erro.equals(ultimoErro)) {
                log.warn("Telegram: falha ao buscar mensagens do bot — {}", erro);
                ultimoErro = erro;
            }
            return;
        }
        for (TelegramClient.Atualizacao atualizacao : atualizacoes) {
            // offset = último update + 1: o Telegram não manda de novo o que já foi lido
            proximoOffset = Math.max(proximoOffset, atualizacao.updateId() + 1);

            TelegramClient.Mensagem mensagem = atualizacao.message();
            if (mensagem == null || mensagem.chat() == null) {
                continue;
            }
            long chatId = mensagem.chat().id();
            String nome = mensagem.chat().primeiroNome() == null ? "" : ", " + mensagem.chat().primeiroNome();
            telegramClient.enviarMensagem(String.valueOf(chatId), """
                    Olá%s! 👋 Sou o bot de avisos da barbearia.

                    Seu ID do Telegram é: <code>%d</code>

                    Passe esse número para o administrador cadastrar no painel \
                    (Barbeiros → ID do Telegram). A partir daí você recebe aqui cada novo agendamento."""
                    .formatted(nome.replace("<", "").replace(">", "").replace("&", ""), chatId));
        }
    }
}
