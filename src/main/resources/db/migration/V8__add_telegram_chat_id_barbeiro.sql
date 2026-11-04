-- ID do chat do Telegram do barbeiro, para receber aviso de novos agendamentos
ALTER TABLE barbeiro
    ADD COLUMN telegram_chat_id VARCHAR(30);
