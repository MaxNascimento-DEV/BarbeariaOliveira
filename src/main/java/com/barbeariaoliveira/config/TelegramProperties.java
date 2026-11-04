package com.barbeariaoliveira.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bot do Telegram que avisa sobre novos agendamentos.
 *
 * @param habilitado   liga/desliga os avisos (TELEGRAM_HABILITADO)
 * @param token        token do bot criado no @BotFather (TELEGRAM_TOKEN) — segredo!
 * @param chatIdsAdmin chats que recebem TODOS os avisos, ex.: dono da barbearia (TELEGRAM_CHAT_IDS, separados por vírgula)
 * @param painelUrl    link do painel incluído nas mensagens
 */
@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        boolean habilitado,
        String token,
        List<String> chatIdsAdmin,
        String painelUrl) {

    public TelegramProperties {
        chatIdsAdmin = chatIdsAdmin == null
                ? List.of()
                : chatIdsAdmin.stream().map(String::strip).filter(id -> !id.isEmpty()).toList();
    }

    /** Só envia se estiver ligado E com token configurado. */
    public boolean ativo() {
        return habilitado && token != null && !token.isBlank();
    }
}
