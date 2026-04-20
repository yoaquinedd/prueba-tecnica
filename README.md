# API RESTful de Creación de Usuarios

API REST desarrollada con Spring Boot para el registro de usuarios. Permite crear usuarios con validación de email y contraseña, persistencia en base de datos H2 en memoria, y generación de token **JWT** (firmado criptográficamente).

## Tecnologías

- **Java 21**
- **Spring Boot 3.5.13**
- **Spring Data JPA** (Hibernate)
- **H2 Database** (base de datos en memoria)
- **Maven** (gestión de dependencias y build)
- **JJWT** (Generación y firma de JSON Web Tokens)

## Requisitos Previos

- **Java 21** o superior instalado
- **Maven 3.6+** (o usar el wrapper `mvnw` incluido)

## Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd creacion-usuarios
```

### 2. Compilar el proyecto

```bash
./mvnw clean package
```

En Windows:

```bash
mvnw.cmd clean package
```

### 3. Ejecutar la aplicación

Para ejecutar la aplicación con los valores por defecto definidos en el `application.yaml`:

En **Linux/macOS**:
```bash
./mvnw spring-boot:run
```

En **Windows**:
```cmd
mvnw.cmd spring-boot:run
```

> **Opcional:** Adicionalmente, se pueden pasar variables de entorno desde la línea de comandos para sobreescribir los valores por defecto si así se desea. Ejemplo en bash: `JWT_SECRET=TuClave... PORT=8080 ./mvnw spring-boot:run`

La aplicación estará disponible en: **http://localhost:8080**

> **💡 Ejecución desde IntelliJ IDEA:** Si ejecutas el proyecto desde IntelliJ, recuerda agregar estas variables en la configuración de ejecución (`Run/Debug Configurations` > `Environment variables`):
> `JWT_SECRET=UnaClaveMuySecretaParaFirmarLosJWTTokensQueDebeTenerUnLargoDeAlmenos256Bits;PORT=8080`

## Probar la API

Existen varias formas de probar la aplicación una vez levantada:
1. Accediendo a la interfaz visual de **Swagger / OpenAPI** en: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
2. Copiando el comando `curl` de ejemplo mostrado abajo y pegándolo en **Postman** (Import -> Raw text) o en tu terminal.

## Endpoint de Registro

### `POST /api/v1/users`

Crea un nuevo usuario en el sistema.

#### Ejemplo de Request

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "juan@rodriguez.cl",
    "password": "hunter2",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }'
```

> **Nota:** El email y la contraseña deben cumplir estrictamente con las expresiones regulares definidas en `application.yaml` (por defecto, email con dominio `.cl` y contraseña alfanumérica sin espacios).

#### Respuesta Exitosa — `201 Created`

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Juan Rodriguez",
  "email": "juan@rodriguez.cl",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ],
  "created": "2026-04-18T23:00:00",
  "modified": "2026-04-18T23:00:00",
  "last_login": "2026-04-18T23:00:00",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQHJvZHJpZ3Vlei5jbCIsImlhdCI...",
  "isactive": true
}
```

#### Respuestas de Error

Todos los errores retornan el siguiente formato JSON:

```json
{
  "mensaje": "descripción del error"
}
```

| Código | Mensaje | Causa |
|--------|---------|-------|
| `400 Bad Request` | `Formato de email no valido` | El email no cumple la regex configurada |
| `400 Bad Request` | `Formato de password no valido` | La contraseña no cumple la regex configurada |
| `422 Unprocessable Entity` | `El correo ya registrado` | Ya existe un usuario con ese email |
| `500 Internal Server Error` | `Error interno del servidor` | Error inesperado |

## Configuración y Variables de Entorno

La aplicación utiliza variables de entorno para su configuración, permitiendo inyectar valores seguros sin hardcodearlos. Esto se define en `src/main/resources/application.yaml`:

```yaml
server:
  port: ${PORT:8080}
app:
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration-ms: 86400000
  validation:
    email:
      regex: ^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.(cl)$
    password:
      regex: ^[a-zA-Z0-9_]+$
```

*   `JWT_SECRET`: Llave secreta para firmar los tokens JWT. **Debe tener al menos 256 bits (32 caracteres)** de longitud para cumplir con los estándares del algoritmo HS256.
*   `PORT`: Puerto donde se levantará la aplicación (por defecto 8080).
*   Las expresiones regulares de validación también son directamente configurables en este archivo.

## Base de Datos (H2 Console)

La base de datos en memoria H2 puede ser accedida mientras la aplicación está corriendo:

- **URL:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **JDBC URL:** `jdbc:h2:mem:usersdb`
- **Username:** `sa`
- **Password:** *(dejar en blanco)*

## Diagrama de la Solución

```
┌─────────────────────────────────────────────────────────────────┐
│                         Cliente (cURL / Postman)                │
│                    POST /api/v1/users (JSON)                    │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Spring Boot App                          │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                   UserController                          │  │
│  │              @PostMapping /api/v1/users                   │  │
│  │          Recibe RegisterRequestDTO (JSON)                 │  │
│  └────────────────────────┬──────────────────────────────────┘  │
│                           │                                     │
│                           ▼                                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                   UserServiceImpl                         │  │
│  │  ┌─────────────────────────────────────────────────────┐  │  │
│  │  │  1. Validar formato email (regex configurable)      │  │  │
│  │  │  2. Verificar email no duplicado (Repository)       │  │  │
│  │  │  3. Validar formato password (regex configurable)   │  │  │
│  │  │  4. Mapear DTO → Entity (UserMapper)                │  │  │
│  │  │  5. Hashear password (BCryptPasswordEncoder)        │  │  │
│  │  │  6. Generar token JWT (JwtService)                  │  │  │
│  │  │  7. Persistir usuario (Repository)                  │  │  │
│  │  │  8. Mapear Entity → ResponseDTO (UserMapper)        │  │  │
│  │  └─────────────────────────────────────────────────────┘  │  │
│  └────────────────────────┬──────────────────────────────────┘  │
│                           │                                     │
│              ┌────────────┼────────────┐                        │
│              ▼            ▼            ▼                        │
│  ┌──────────────┐ ┌────────────┐ ┌──────────────────────────┐   │
│  │  UserMapper  │ │ UserRepo   │ │ GlobalExceptionHandler   │   │
│  │  DTO ↔ Entity│ │ JPA/H2     │ │ Errores → {"mensaje":…}  │   │
│  └──────────────┘ └─────┬──────┘ └──────────────────────────┘   │
│                         │                                       │
└─────────────────────────┼───────────────────────────────────────┘
                          │
                          ▼
               ┌─────────────────────┐
               │   H2 Database       │
               │   (en memoria)      │
               │  ┌───────────────┐  │
               │  │ USERS         │  │
               │  │ PHONES        │  │
               │  └───────────────┘  │
               └─────────────────────┘
```

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/prueba/creacion_usuarios/
│   │   ├── CreacionUsuariosApplication.java    # Clase principal
│   │   ├── config/
│   │   │   ├── SecurityConfig.java            # Configuración de Spring Security y BCrypt
│   │   │   └── OpenApiConfig.java             # Configuración de Swagger/OpenAPI
│   │   ├── controller/
│   │   │   └── UserController.java            # Endpoint REST
│   │   ├── dto/
│   │   │   ├── RegisterRequestDTO.java        # DTO de entrada
│   │   │   ├── RegisterResponseDTO.java       # DTO de salida
│   │   │   └── PhoneDTO.java                  # DTO de teléfono
│   │   ├── model/
│   │   │   ├── User.java                      # Entidad JPA Usuario
│   │   │   └── Phone.java                     # Entidad JPA Teléfono
│   │   ├── repository/
│   │   │   └── UserRepository.java            # Repositorio JPA
│   │   ├── service/
│   │   │   ├── UserService.java               # Interfaz del servicio
│   │   │   ├── UserServiceImpl.java           # Implementación
│   │   │   └── JwtService.java                # Generación y firma de tokens JWT
│   │   ├── mapper/
│   │   │   └── UserMapper.java                # Mapeo DTO ↔ Entity
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java     # Manejo global de errores
│   │       ├── EmailAlreadyExistsException.java
│   │       └── InvalidFormatException.java
│   └── resources/
│       ├── application.yaml                   # Variables de entorno y configuraciones
│       └── schema.sql                         # Script DDL de la base de datos
└── test/
    └── java/com/prueba/creacion_usuarios/
        ├── controller/
        │   └── UserControllerTest.java        # Tests unitarios de capa web (@WebMvcTest)
        └── service/
            └── UserServiceImplTest.java       # Tests unitarios de lógica de negocio (Mockito)
```
