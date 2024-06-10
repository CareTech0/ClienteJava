FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .
RUN mvn clean install

FROM openjdk:23-ea-17-jdk-oraclelinux9

WORKDIR /app

COPY --from=build /app/target/cliente-java-caretech-1.0-SNAPSHOT-jar-with-dependencies.jar .

ENTRYPOINT ["java", "-jar", "cliente-java-caretech-1.0-SNAPSHOT-jar-with-dependencies.jar"]