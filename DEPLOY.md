# Deploy — Barbearia Oliveira

**API + PostgreSQL no Railway · Site (front) na Vercel.** Tempo estimado: 30–40 min.

```
Cliente ──► Vercel (site React) ──► Railway (API Spring Boot) ──► Railway (PostgreSQL)
                                          │
                                          └──► Telegram (avisos)
```

Antes de começar:
- O código da API e o do front precisam estar no **GitHub** (um repositório para cada pasta).
- Tenha à mão os valores que você já usa no seu computador: `ADMIN_EMAIL`, `ADMIN_SENHA_HASH` e `TELEGRAM_TOKEN`.

---

## 1. Banco + API no Railway

1. Crie a conta em **railway.com** entrando com o GitHub e escolha o plano **Hobby**.
2. **New Project → Deploy PostgreSQL**. O banco fica pronto em segundos.
3. No mesmo projeto: **+ Create → GitHub Repo →** escolha o repositório da API (`barbearia-oliveira`).
   O Railway encontra o `Dockerfile` e o `railway.json` e começa o build — **o primeiro deploy vai falhar** porque as variáveis ainda não existem. Normal.
4. Clique no serviço da API → **Variables → Raw Editor** e cole (troque o que está entre `< >`):

   ```
   DB_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
   DB_USERNAME=${{Postgres.PGUSER}}
   DB_PASSWORD=${{Postgres.PGPASSWORD}}

   JWT_SECRET=<gere uma NOVA chave — veja abaixo>
   ADMIN_EMAIL=<seu e-mail de admin>
   ADMIN_SENHA_HASH=<o mesmo hash BCrypt que você usa no computador>

   BARBEARIA_WHATSAPP=5581984920075
   CONFIAR_PROXY=true
   CORS_ORIGENS=https://<preencha no passo 3.4>

   TELEGRAM_HABILITADO=true
   TELEGRAM_TOKEN=<token do bot>
   TELEGRAM_CHAT_IDS=<seu ID do Telegram, opcional>
   PAINEL_URL=https://<preencha no passo 3.4>/admin
   ```
   - `${{Postgres.PGHOST}}` etc. são **referências**: o Railway preenche sozinho com os dados do banco. Se o seu serviço de banco tiver outro nome, troque `Postgres` pelo nome dele.
   - **JWT_SECRET novo** (não reutilize o do computador). No PowerShell:
     ```powershell
     $b = New-Object byte[] 64; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)
     ```
   - **ADMIN_SENHA_HASH**: para ver o que você já usa, rode `echo $env:ADMIN_SENHA_HASH` no PowerShell.
     Ele só é usado uma vez, quando a migration V6 cria o admin no banco novo.
5. **Settings → Networking → Generate Domain**. Anote o endereço, ex.: `https://barbearia-oliveira-production.up.railway.app`.
6. **Deployments → Redeploy**. Nos logs, confira:
   - `Successfully applied 9 migrations` (Flyway criou o banco)
   - `Telegram: LIGADO como @ClienteBarberOliveiraBot`
7. Teste no navegador: `https://<endereço-da-api>/actuator/health` deve mostrar `{"status":"UP"}`.

> ⚠️ **Telegram em dois lugares:** um bot só pode ser lido por **uma** API por vez.
> No seu computador, deixe `TELEGRAM_HABILITADO=false` (ou crie um segundo bot só para testes),
> senão a API local e a de produção “brigam” pelas mensagens.

## 2. Site na Vercel

1. Crie a conta em **vercel.com** entrando com o GitHub.
2. **Add New → Project →** escolha o repositório do front. O framework **Vite** é detectado sozinho.
3. Em **Environment Variables**, adicione:
   ```
   VITE_API_URL=https://<endereço-da-api-do-passo-1.5>
   ```
   (sem `/` no final)
4. **Deploy**. Anote o endereço, ex.: `https://barbearia-oliveira.vercel.app`.

## 3. Ligar um no outro

1. Volte ao Railway → API → **Variables** e preencha:
   ```
   CORS_ORIGENS=https://barbearia-oliveira.vercel.app
   PAINEL_URL=https://barbearia-oliveira.vercel.app/admin
   ```
2. Salve — o Railway faz o redeploy sozinho.

## 4. Conferir

- [ ] Abrir o site e ver serviços e barbeiros (se aparecer vazio, é CORS: confira `CORS_ORIGENS`, sem `/` no final).
- [ ] Entrar em `/admin/login` com o admin.
- [ ] Cadastrar barbeiros, serviços e os **horários de trabalho** (banco novo começa vazio).
- [ ] Criar os logins dos barbeiros em **Usuários**.
- [ ] Fazer um agendamento de teste e ver o aviso no painel e no Telegram.

## Domínio próprio (opcional)

Registre no **registro.br** (ex.: `barbeariaoliveira.com.br`) e:
- Vercel → Project → **Settings → Domains** → adicione o domínio e siga as instruções de DNS.
- Atualize no Railway `CORS_ORIGENS` e `PAINEL_URL` para o domínio novo.

## Atualizações

Cada `git push` na branch `main` faz o deploy automático nos dois lados.
Mudança no banco = **nova migration** (V10, V11…). Nunca edite uma migration que já rodou.

## Problemas comuns

| Sintoma | Causa provável |
|---|---|
| Build falha no Railway | Veja o log do build; costuma ser erro de compilação que também aparece no `mvnw package` local |
| `FlywayException ... placeholder` | Faltou `ADMIN_EMAIL` ou `ADMIN_SENHA_HASH` |
| `Connection refused` ao banco | `DB_URL` com nome errado do serviço (`${{Postgres...}}`) |
| Site carrega mas não lista nada | CORS ou `VITE_API_URL` errado (redeploy na Vercel após mudar variável) |
| `/admin` dá 404 ao dar F5 | Falta o `vercel.json` no repositório do front |
| Telegram `409 Conflict` nos logs | Outra API (ex.: a do seu computador) está lendo o mesmo bot |
