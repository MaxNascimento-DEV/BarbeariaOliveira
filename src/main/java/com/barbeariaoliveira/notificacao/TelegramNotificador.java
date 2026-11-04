package com.barbeariaoliveira.notificacao;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.barbeariaoliveira.config.TelegramProperties;

/**
 * Escuta os eventos de agendamento e avisa no Telegram:
 * o barbeiro do horário (se tiver o ID cadastrado) e os chats de admin (TELEGRAM_CHAT_IDS).
 *
 * AFTER_COMMIT: só avisa se o agendamento foi mesmo salvo.
 * @Async: o cliente não espera o Telegram responder.
 */
@Component
public class TelegramNotificador {

    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("EEEE, dd/MM 'às' HH:mm", PT_BR);

    private final TelegramProperties properties;
    private final TelegramClient telegramClient;

    public TelegramNotificador(TelegramProperties properties, TelegramClient telegramClient) {
        this.properties = properties;
        this.telegramClient = telegramClient;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void aoMudarAgendamento(AvisoAgendamento aviso) {
        if (!properties.ativo()) {
            return;
        }
        String mensagem = montarMensagem(aviso);

        // Set evita mandar duas vezes quando o barbeiro também é admin
        Set<String> destinos = new LinkedHashSet<>();
        if (aviso.telegramChatIdBarbeiro() != null && !aviso.telegramChatIdBarbeiro().isBlank()) {
            destinos.add(aviso.telegramChatIdBarbeiro().strip());
        }
        destinos.addAll(properties.chatIdsAdmin());

        for (String chatId : destinos) {
            telegramClient.enviarMensagem(chatId, mensagem);
        }
    }

    String montarMensagem(AvisoAgendamento a) {
        String quando = FORMATO_DATA.format(a.inicio());
        String valor = a.valor() == null ? "" : " · " + NumberFormat.getCurrencyInstance(PT_BR).format(a.valor());
        String painel = linkPublico(properties.painelUrl())
                ? "\n\n<a href=\"" + escapar(properties.painelUrl()) + "\">Abrir painel</a>"
                : "";

        return switch (a.tipo()) {
            case NOVO -> """
                    💈 <b>Novo agendamento</b> #%d

                    👤 %s
                    📞 %s
                    ✂️ %s com %s%s
                    📅 %s

                    ⏱️ Confirme no painel em até 20 min, senão o horário é liberado.%s"""
                    .formatted(a.agendamentoId(), escapar(a.nomeCliente()), formatarTelefone(a.telefoneCliente()),
                            escapar(a.nomeServico()), escapar(a.nomeBarbeiro()), valor, quando, painel);
            case CANCELADO_SEM_CONFIRMACAO -> """
                    ⚠️ <b>Agendamento cancelado</b> #%d
                    Não foi confirmado a tempo e o horário foi liberado.

                    👤 %s · %s
                    ✂️ %s com %s
                    📅 %s"""
                    .formatted(a.agendamentoId(), escapar(a.nomeCliente()), formatarTelefone(a.telefoneCliente()),
                            escapar(a.nomeServico()), escapar(a.nomeBarbeiro()), quando);
        };
    }

    /** O Telegram recusa a mensagem inteira se o link for localhost/IP local. */
    private static boolean linkPublico(String url) {
        return url != null
                && url.startsWith("http")
                && !url.contains("localhost")
                && !url.contains("127.0.0.1");
    }

    /** O parse_mode HTML do Telegram exige escapar &, < e >. */
    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String formatarTelefone(String digitos) {
        if (digitos == null) {
            return "";
        }
        if (digitos.length() == 11) {
            return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, 7) + "-" + digitos.substring(7);
        }
        if (digitos.length() == 10) {
            return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, 6) + "-" + digitos.substring(6);
        }
        return escapar(digitos);
    }
}
