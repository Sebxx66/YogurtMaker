# Etapa 1: compila el proyecto
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline
COPY src src
RUN ./mvnw -B -q package -DskipTests

# Etapa 2: imagen liviana solo con el JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 app
COPY --from=build /app/target/yogurt-maker-*.jar app.jar
USER app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
