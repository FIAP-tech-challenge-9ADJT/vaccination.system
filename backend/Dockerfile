# Etapa de cache
FROM maven:3.9.7-eclipse-temurin-21 AS dependency-builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Etapa de build
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY --from=dependency-builder /root/.m2 /root/.m2
COPY src ./src
COPY pom.xml .
RUN mvn clean package jacoco:report

# Etapa de execução
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]