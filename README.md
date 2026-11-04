# Barbearia Oliveira — API

API Spring Boot 4 (Java 21) + PostgreSQL do sistema de agendamento.

## Como rodar

1. PostgreSQL rodando com o banco `barbeariaOliveira`.
2. Variáveis: já configuradas no Windows **ou** no arquivo `.env` (modelo em `.env.example`).
3. Dê dois cliques em **`rodar-tudo.cmd`** — sobe a API (porta 8080) e o front (`D:\Projects_Faculdade\FrontEnd`, porta 5173) e abre o navegador.
   - Só a API: `rodar-backend.cmd` (ou `mvnw.cmd spring-boot:run`).

Swagger: http://localhost:8080/swagger-ui.html

## Primeiro uso

- Entre em http://localhost:5173/admin/login com o admin criado pela migration V6.
- Em **Horários**, cadastre o expediente de cada barbeiro (sem isso, o site mostra todos os dias como folga).
- Em **Serviços**, ajuste ícone, categoria e descrição.

## Avisos de novos agendamentos

### No painel (sem configurar nada)
Com o painel aberto, a cada novo agendamento toca um som, aparece uma notificação do navegador
e o número de novos aparece em **Agenda do dia** e no título da aba.
Clique em **Ativar notificações** (menu lateral) uma vez e aceite a permissão do navegador.

### No Telegram (celular do barbeiro, mesmo com o app fechado)
1. No Telegram, abra o **@BotFather**, envie `/newbot`, escolha um nome (ex.: *Barbearia Oliveira Avisos*)
   e um usuário terminado em `bot`. Ele responde com o **token** — é um segredo, não compartilhe.
2. Defina as variáveis (no Windows ou no `.env`) e reinicie a API:
   ```
   TELEGRAM_HABILITADO=true
   TELEGRAM_TOKEN=<token do BotFather>
   ```
3. Cada barbeiro abre o bot no Telegram e manda **oi**. O bot responde com o **ID** dele.
4. No painel, em **Barbeiros → editar**, cole o número em **ID do Telegram**.
   A partir daí ele recebe os agendamentos dele.
5. (Opcional) Para o dono receber **todos** os avisos, coloque o ID dele em `TELEGRAM_CHAT_IDS`
   (vários separados por vírgula).

O bot avisa quando entra um agendamento e quando um agendamento é cancelado automaticamente
por não ter sido confirmado a tempo.
