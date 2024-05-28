# Etapa 2: Execução
FROM eclipse-temurin:17.0.10_7-jre

WORKDIR /app

# Copie o jar construído da fase de build
COPY ./target/cliente-java-caretech-1.0-SNAPSHOT-jar-with-dependencies.jar ./my-app.jar

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "my-app.jar"]