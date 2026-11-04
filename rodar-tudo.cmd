@echo off
cd /d "%~dp0"
set "FRONT=%~dp0..\..\FrontEnd"
start "API - Barbearia Oliveira" cmd /k mvnw.cmd spring-boot:run
start "Front - Barbearia Oliveira" /d "%FRONT%" cmd /k "if not exist node_modules (npm install) & npm run dev"
echo Subindo a API (8080) e o site (5173)... o navegador abre em 30 segundos.
timeout /t 30 /nobreak >nul
start "" http://localhost:5173
