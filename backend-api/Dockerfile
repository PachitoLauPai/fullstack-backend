# Paso 1: Usar una imagen de Maven con Java 17 para compilar el proyecto
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Usamos 'mvn' directamente en lugar de './mvnw' por si hay problemas de permisos de ejecución
RUN mvn clean package -DskipTests

# Paso 2: Usar una imagen ligera de Java 17 para ejecutar el .jar
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/backend-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]