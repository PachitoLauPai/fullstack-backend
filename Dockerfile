# --- Etapa 1: Compilación del proyecto ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el archivo de configuración de Maven y el código fuente
COPY pom.xml .
COPY src ./src

# Compilar el proyecto saltándose los tests para acelerar el despliegue en Render
RUN mvn clean package -DskipTests

# --- Etapa 2: Imagen final ligera para ejecución ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar el archivo .jar generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto 8080 (el puerto por defecto de Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación aplicando optimizaciones de memoria para Render
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]