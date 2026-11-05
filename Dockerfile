# ---------- 1) Build: compila o projeto com Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Baixa as dependências primeiro (fica em cache enquanto o pom.xml não muda)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- 2) Execução: só o Java (JRE), imagem bem menor ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Não roda como root
RUN useradd --system --uid 1001 app
USER app

COPY --from=build /app/target/*.jar app.jar

# Usa até 75% da memória do container para o Java
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
