package com.barbeariaoliveira.notificacao;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.barbeariaoliveira.config.BarbeariaProperties;
import com.barbeariaoliveira.entity.Agendamento;
import com.barbeariaoliveira.util.TelefoneUtils;

/**
 * Monta links "clique para conversar" do WhatsApp (https://wa.me/...), que abrem o
 * WhatsApp de quem clica já com a mensagem escrita. Não usa API, não tem custo
 * e não depende de cadastro na Meta: quem aperta "enviar" é uma pessoa.
 */
@Component
public class WhatsAppLinkBuilder {

    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("EEEE, dd/MM", PT_BR);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm", PT_BR);

    private final BarbeariaProperties barbeariaProperties;

    public WhatsAppLinkBuilder(BarbeariaProperties barbeariaProperties) {
        this.barbeariaProperties = barbeariaProperties;
    }

    /** Cliente -> barbearia: o cliente avisa a barbearia que agendou. */
    public String linkParaBarbearia(Agendamento agendamento, LocalDateTime inicioLocal) {
        String texto = """
                Olá! Acabei de agendar pelo site:

                👤 %s
                ✂️ %s com %s
                📅 %s às %s

                Código do agendamento: #%d""".formatted(
                agendamento.getNomeCliente(),
                agendamento.getServico().getNome(),
                agendamento.getBarbeiro().getNome(),
                FORMATO_DATA.format(inicioLocal),
                FORMATO_HORA.format(inicioLocal),
                agendamento.getId());

        return montarLink(barbeariaProperties.whatsapp(), texto);
    }

    /** Barbearia -> cliente: a mensagem muda conforme o status do agendamento. */
    public String linkParaCliente(Agendamento agendamento, LocalDateTime inicioLocal) {
        String nome = agendamento.getNomeCliente();
        String barbearia = barbeariaProperties.nome();
        String detalhes = "✂️ %s com %s\n📅 %s às %s".formatted(
                agendamento.getServico().getNome(),
                agendamento.getBarbeiro().getNome(),
                FORMATO_DATA.format(inicioLocal),
                FORMATO_HORA.format(inicioLocal));

        String texto = switch (agendamento.getStatus()) {
            case AGENDADO -> "Olá, %s! Recebemos seu agendamento na %s:\n\n%s\n\nEm breve confirmaremos. Código: #%d"
                    .formatted(nome, barbearia, detalhes, agendamento.getId());
            case CONFIRMADO -> "Olá, %s! Seu horário na %s está confirmado ✅\n\n%s\n\nTe esperamos!"
                    .formatted(nome, barbearia, detalhes);
            case EM_ANDAMENTO, CONCLUIDO -> "Olá, %s! Obrigado pela visita à %s. Esperamos te ver em breve! 💈"
                    .formatted(nome, barbearia);
            case CANCELADO -> "Olá, %s. Seu agendamento na %s foi cancelado:\n\n%s\n\nPara remarcar, é só acessar nosso site."
                    .formatted(nome, barbearia, detalhes);
        };

        return montarLink(agendamento.getTelefoneCliente(), texto);
    }

    private String montarLink(String telefone, String texto) {
        String numero = TelefoneUtils.paraWhatsApp(telefone);
        if (numero == null) {
            return null; // ex.: telefone da barbearia ainda não configurado
        }
        String textoCodificado = URLEncoder.encode(texto, StandardCharsets.UTF_8).replace("+", "%20");
        return "https://wa.me/" + numero + "?text=" + textoCodificado;
    }
}
