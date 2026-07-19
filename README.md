# 🖥️ Backend API — Sistema de Visitas Inopinadas

API REST construida con **Spring Boot 4** + **MySQL** + **JWT Authentication**.

---

## 📋 Requisitos previos

Asegúrate de tener instalado lo siguiente antes de ejecutar el proyecto:

| Herramienta | Versión mínima | Descarga |
|---|---|---|
| Java JDK | 21 | [adoptium.net](https://adoptium.net/) |
| Maven | 3.9+ *(incluido en el proyecto)* | — |
| MySQL | 8.0+ | [mysql.com](https://dev.mysql.com/downloads/) |

> **Nota:** No necesitas instalar Maven globalmente, el proyecto incluye `mvnw` (Maven Wrapper).

---

## ⚙️ Configuración inicial

### 1. Clonar / Abrir el proyecto

```bash
cd fullstack-backend
```

### 2. Configurar la base de datos

Abre MySQL y crea el usuario/base de datos (o usa el archivo SQL incluido):

```sql
-- Opcional: importar esquema completo
SOURCE bd.sql;
```

> La aplicación crea la base de datos automáticamente si no existe gracias a `createDatabaseIfNotExist=true`.

### 3. Configurar `application.properties`

Edita el archivo `src/main/resources/application.properties` con tus credenciales:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/db_visitas_inopinadas?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root          # ← Cambia si tu usuario es diferente
spring.datasource.password=1234          # ← Cambia por tu contraseña de MySQL

# JWT
jwt.secret=miClaveSecretaSuperSeguraParaJWT2024   # ← Cambia en producción

# Puerto del servidor (por defecto 8080)
server.port=8080

# Email (Gmail SMTP)
spring.mail.username=tu_correo@gmail.com          # ← Tu correo
spring.mail.password=xxxx xxxx xxxx xxxx          # ← App Password de Google
```

#### Cómo obtener el App Password de Gmail:
1. Ve a [myaccount.google.com](https://myaccount.google.com) → Seguridad
2. Activa **Verificación en dos pasos**
3. Ve a **Contraseñas de aplicaciones** y genera una nueva

---

## 🚀 Ejecutar el proyecto

### Opción A — Con Maven Wrapper (recomendado)

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

### Opción B — Desde IntelliJ IDEA / VS Code

1. Abre la carpeta `fullstack-backend` en tu IDE
2. Ejecuta la clase principal `BackendApiApplication.java`

### Opción C — Compilar y ejecutar el JAR

```bash
mvnw.cmd package -DskipTests
java -jar target/backend-api-0.0.1-SNAPSHOT.jar
```

---

## ✅ Verificar que está funcionando

Una vez iniciado, el backend estará disponible en:

```
http://localhost:8080
```

Puedes probar el endpoint de salud:
```
GET http://localhost:8080/actuator/health
```

---

## 📁 Estructura del proyecto

```
fullstack-backend/
├── src/
│   └── main/
│       ├── java/com/visitas/   ← Código fuente Java
│       └── resources/
│           └── application.properties  ← Configuración
├── bd.sql                       ← Script SQL de la base de datos
├── pom.xml                      ← Dependencias Maven
├── mvnw / mvnw.cmd              ← Maven Wrapper
└── Dockerfile                   ← Contenedor Docker (opcional)
```

---

## 📦 Dependencias principales

| Librería | Uso |
|---|---|
| Spring Boot 4.1 | Framework principal |
| Spring Security + JWT | Autenticación y autorización |
| Spring Data JPA + Hibernate | ORM para MySQL |
| MySQL Connector | Driver de base de datos |
| Lombok | Reducción de código boilerplate |
| MapStruct | Mapeo de DTOs |
| Apache POI | Exportación a Excel |
| iText 7 | Generación de PDFs |
| Apache PDFBox | Procesamiento de PDFs |
| Spring Mail | Envío de correos electrónicos |

---

## 🐳 Docker (opcional)

Si prefieres usar Docker:

```bash
# Construir imagen
docker build -t visitas-backend .

# Ejecutar contenedor
docker run -p 8080:8080 visitas-backend
```

---

## ❓ Problemas comunes

| Error | Solución |
|---|---|
| `Access denied for user 'root'@'localhost'` | Verifica usuario y contraseña en `application.properties` |
| `Communications link failure` | Asegúrate de que MySQL esté corriendo |
| `Port 8080 already in use` | Cambia `server.port` o detén el proceso que usa ese puerto |
| `Could not autowire` / compilación fallida | Asegúrate de usar **Java 21** (`java -version`) |
