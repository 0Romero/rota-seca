# Etapa 1: compila o projeto com Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# Etapa 2: imagem final, só com o Java e o .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV TZ=America/Sao_Paulo
COPY --from=build /app/target/rota-seca-api.jar app.jar
EXPOSE 8080
# -Xmx baixo para caber no plano gratuito do Render (512 MB)
ENTRYPOINT ["java", "-Xmx300m", "-Duser.timezone=America/Sao_Paulo", "-jar", "app.jar"]
