@echo off
title API - Barbearia Oliveira
cd /d "%~dp0"
echo Iniciando a API em http://localhost:8080 ...
call mvnw.cmd spring-boot:run
pause
