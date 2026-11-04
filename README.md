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
