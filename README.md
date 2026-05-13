<!-- Improved back-to-top link -->
<a id="readme-top"></a>

<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stars][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

---

<!-- PROJECT LOGO & TITLE -->
<br />
<div align="center">

  <h1>🥛 Yogurt Maker API</h1>

  <p align="center">
    REST API para la gestión completa del proceso de fabricación de yogurt artesanal.<br/>
    Control de recetas, lotes, temperaturas y monitoreo en tiempo real.
    <br /><br />
    <a href="#documentación-api"><strong>Explorar la documentación »</strong></a>
    <br /><br />
    <a href="#endpoints-principales">Ver Endpoints</a>
    &middot;
    <a href="https://github.com/JhoanDev87/yogurt-maker/issues/new?labels=bug">Reportar Bug</a>
    &middot;
    <a href="https://github.com/JhoanDev87/yogurt-maker/issues/new?labels=enhancement">Solicitar Feature</a>
  </p>
</div>

---

<!-- TABLE OF CONTENTS -->
<details>
  <summary>📋 Tabla de Contenidos</summary>
  <ol>
    <li><a href="#sobre-el-proyecto">Sobre el Proyecto</a></li>
    <li><a href="#arquitectura">Arquitectura</a></li>
    <li><a href="#tecnologías">Tecnologías</a></li>
    <li><a href="#requisitos-previos">Requisitos Previos</a></li>
    <li><a href="#instalación-y-ejecución">Instalación y Ejecución</a></li>
    <li><a href="#configuración">Configuración</a></li>
    <li><a href="#endpoints-principales">Endpoints Principales</a></li>
    <li><a href="#flujo-de-fabricación">Flujo de Fabricación</a></li>
    <li><a href="#documentación-api">Documentación API</a></li>
    <li><a href="#estructura-del-proyecto">Estructura del Proyecto</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contribuir">Contribuir</a></li>
    <li><a href="#licencia">Licencia</a></li>
    <li><a href="#contacto">Contacto</a></li>
  </ol>
</details>

---

## Sobre el Proyecto

**Yogurt Maker API** es una API REST desarrollada con **Spring Boot 4** que permite gestionar de forma integral el proceso de fabricación de yogurt artesanal o a pequeña escala industrial.

El sistema automatiza y monitorea cada etapa del proceso productivo: desde la definición de recetas hasta el control de temperatura durante la incubación, pasando por la trazabilidad completa de cada lote de producción.

### ¿Qué resuelve?

- **Gestión de recetas**: Crea, actualiza y organiza recetas con parámetros detallados de proceso (temperaturas, tiempos, ingredientes).
- **Control de lotes**: Sigue el ciclo de vida completo de cada lote desde `PREPARING` hasta `COMPLETED`.
- **Monitoreo de temperatura**: Registra y consulta logs de temperatura en cada fase del proceso (calentamiento, enfriamiento, incubación).
- **Dashboard de producción**: Visualiza métricas en tiempo real de todos los lotes activos y completados.
- **Trazabilidad total**: Cada lote tiene un código único (`YB-<timestamp>`) con timestamps de cada transición de estado.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Arquitectura

El proyecto sigue una **arquitectura en capas** orientada al dominio:

```
com.JhoanDev87.demo
├── domain/
│   ├── controller/     → Capa de presentación (REST Controllers)
│   ├── model/          → Entidades JPA del dominio
│   ├── repository/     → Interfaces Spring Data JPA
│   └── service/        → Lógica de negocio
├── dto/                → Data Transfer Objects
└── exception/          → Manejo global de excepciones
```

**Modelos principales:**

| Entidad | Descripción |
|---|---|
| `Recipe` | Receta con parámetros de proceso, ingredientes y niveles de dificultad |
| `YogurtBatch` | Lote de producción con ciclo de vida completo y trazabilidad |
| `Ingredient` | Ingrediente vinculado a una receta (cantidad, unidad, opcionalidad) |
| `TemperatureLog` | Registro de temperatura por lote y por tipo de fase |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| [![Spring Boot][springboot-shield]][springboot-url] | 4.0.6 | Framework principal |
| [![Java][java-shield]][java-url] | 21 | Lenguaje |
| [![H2][h2-shield]][h2-url] | Runtime | Base de datos en memoria |
| [![Lombok][lombok-shield]][lombok-url] | Latest | Reducción de boilerplate |
| [![Swagger][swagger-shield]][swagger-url] | 2.8.6 | Documentación API (SpringDoc OpenAPI) |
| [![Maven][maven-shield]][maven-url] | Wrapper | Gestión de dependencias y build |

**Dependencias Spring Boot:**
- `spring-boot-starter-web` — API REST con MVC
- `spring-boot-starter-data-jpa` — Persistencia con Hibernate
- `spring-boot-starter-validation` — Validación de DTOs
- `spring-boot-starter-actuator` — Health checks y métricas
- `springdoc-openapi-starter-webmvc-ui` — Swagger UI automático

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java 21** o superior
  ```sh
  java -version
  # java version "21.x.x"
  ```
- **Maven 3.9+** (o usar el wrapper incluido `./mvnw`)
- **Git**

> El proyecto usa **H2** como base de datos en memoria, por lo que **no requiere ninguna base de datos externa** instalada.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Instalación y Ejecución

### 1. Clonar el repositorio

```sh
git clone https://github.com/JhoanDev87/yogurt-maker.git
cd yogurt-maker/yogurt-maker-main
```

### 2. Compilar el proyecto

```sh
./mvnw clean install
# En Windows:
mvnw.cmd clean install
```

### 3. Ejecutar la aplicación

```sh
./mvnw spring-boot:run
```

La API estará disponible en: **`http://localhost:8081`**

### 4. Verificar que está corriendo

```sh
curl http://localhost:8081/actuator/health
# {"status":"UP"}
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Configuración

El archivo de configuración principal se encuentra en:

```
src/main/resources/application.properties
```

| Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `server.port` | `8081` | Puerto de la aplicación |
| `spring.h2.console.path` | `/h2-console` | Ruta de la consola H2 |
| `spring.jpa.show-sql` | `true` | Muestra SQL en consola |
| `spring.jpa.hibernate.ddl-auto` | `create-drop` | Recrea el esquema al iniciar |
| `spring.main.lazy-initialization` | `false` | Inicialización eager de beans |

### Consola H2

Accede a la base de datos en memoria desde el navegador:

```
URL:      http://localhost:8081/h2-console
JDBC URL: jdbc:h2:mem:yogurtdb;DB_CLOSE_DELAY=-1;MODE=MySQL
Usuario:  sa
Password: (vacío)
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Endpoints Principales

### 🍽️ Recetas — `/api/recipes`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/recipes` | Crear nueva receta |
| `GET` | `/api/recipes` | Listar recetas activas |
| `GET` | `/api/recipes/{id}` | Obtener receta por ID |
| `PUT` | `/api/recipes/{id}` | Actualizar receta |
| `GET` | `/api/recipes/search?keyword=` | Buscar recetas |
| `PATCH` | `/api/recipes/{id}/activate` | Activar receta |
| `PATCH` | `/api/recipes/{id}/deactivate` | Desactivar receta |

**Ejemplo — Crear receta:**
```json
POST /api/recipes
{
  "name": "Yogurt Natural Clásico",
  "description": "Receta base para yogurt natural",
  "defaultMilkVolume": 1.0,
  "defaultStarterAmount": 2.0,
  "heatingTemperature": 85.0,
  "heatingDuration": 30,
  "inoculationTemperature": 43.0,
  "incubationTemperature": 42.0,
  "minIncubationTime": 6,
  "maxIncubationTime": 8,
  "refrigerationTime": 4,
  "difficulty": "BEGINNER",
  "tips": "Asegúrate de mantener la temperatura estable durante la incubación",
  "ingredients": [
    { "name": "Leche entera", "quantity": 1.0, "unit": "litros", "optional": false },
    { "name": "Cultivo iniciador", "quantity": 2.0, "unit": "cucharadas", "optional": false }
  ]
}
```

---

### 🏭 Lotes — `/api/batches`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/batches` | Iniciar nuevo lote |
| `GET` | `/api/batches` | Listar todos los lotes |
| `GET` | `/api/batches?status=INCUBATING` | Filtrar por estado |
| `GET` | `/api/batches/{id}` | Obtener lote por ID |
| `POST` | `/api/batches/{id}/heating` | Iniciar calentamiento |
| `POST` | `/api/batches/{id}/inoculating` | Iniciar inoculación |
| `POST` | `/api/batches/{id}/incubation` | Iniciar incubación |
| `POST` | `/api/batches/{id}/refrigeration` | Iniciar refrigeración |
| `POST` | `/api/batches/{id}/complete` | Completar lote |
| `POST` | `/api/batches/{id}/fail` | Marcar como fallido |
| `POST` | `/api/batches/{id}/temperature` | Registrar temperatura |

**Ejemplo — Iniciar lote:**
```json
POST /api/batches
{
  "recipeId": 1,
  "customMilkVolume": 2.0,
  "customStarterAmount": 4.0
}
```

**Ejemplo — Registrar temperatura:**
```json
POST /api/batches/1/temperature
{
  "temperature": 42.5,
  "type": "INCUBATION"
}
```

---

### 📊 Monitoreo — `/api/monitoring`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/monitoring/dashboard` | Panel de control general |
| `GET` | `/api/monitoring/batches/active` | Lotes activos en proceso |
| `GET` | `/api/monitoring/batches/{id}/temperature` | Resumen de temperatura |
| `GET` | `/api/monitoring/batches/{id}/temperature-logs` | Historial de temperaturas |
| `GET` | `/api/monitoring/batches/{id}/temperature-logs?start=&end=` | Historial por rango de fechas |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Flujo de Fabricación

Cada lote sigue un ciclo de vida estricto con las siguientes transiciones de estado:

```
PREPARING → HEATING → COOLING → INOCULATING → INCUBATING → REFRIGERATING → COMPLETED
                                                                          ↘
                                                                          FAILED (desde cualquier estado)
```

| Estado | Descripción |
|---|---|
| `PREPARING` | Lote creado, ingredientes listos |
| `HEATING` | Leche calentándose a ~85°C (pasteurización) |
| `COOLING` | Enfriamiento hasta temperatura de inoculación (~43°C) |
| `INOCULATING` | Aplicación del cultivo iniciador |
| `INCUBATING` | Fermentación a temperatura constante (~42°C) |
| `REFRIGERATING` | Refrigeración final para detener la fermentación |
| `COMPLETED` | Yogurt listo ✅ |
| `FAILED` | Lote descartado con razón registrada ❌ |

> **Nota:** Las transiciones son secuenciales y validadas. No es posible pasar de `PREPARING` directamente a `INCUBATING` sin seguir el orden correcto.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Documentación API

El proyecto incluye **Swagger UI** generado automáticamente por SpringDoc OpenAPI.

Una vez que la aplicación esté corriendo, accede en:

```
http://localhost:8081/swagger-ui/index.html
```

También puedes consumir la especificación OpenAPI en formato JSON:

```
http://localhost:8081/v3/api-docs
```

Los controladores están organizados en los siguientes tags dentro de Swagger:
- **Yogurt Batches** — Gestión del ciclo de vida de lotes
- **Recipes** — CRUD de recetas con búsqueda
- **Monitoring** — Métricas y dashboard de producción

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Estructura del Proyecto

```
yogurt-maker-main/
├── src/
│   └── main/
│       ├── java/com/JhoanDev87/demo/
│       │   ├── DemoApplication.java
│       │   ├── domain/
│       │   │   ├── controller/
│       │   │   │   ├── MonitoringController.java
│       │   │   │   ├── RecipeController.java
│       │   │   │   └── YogurtBatchController.java
│       │   │   ├── model/
│       │   │   │   ├── Ingredient.java
│       │   │   │   ├── Recipe.java
│       │   │   │   ├── TemperatureLog.java
│       │   │   │   └── YogurtBatch.java
│       │   │   ├── repository/
│       │   │   │   ├── RecipeRepository.java
│       │   │   │   ├── TemperatureLogRepository.java
│       │   │   │   └── YogurtBatchRepository.java
│       │   │   └── service/
│       │   │       ├── RecipeService.java
│       │   │       ├── TemperatureControlService.java
│       │   │       └── YogurtMakingService.java
│       │   ├── dto/
│       │   │   ├── BatchDTO.java
│       │   │   ├── IngredientDTO.java
│       │   │   ├── MonitoringDTO.java
│       │   │   ├── RecipeDTO.java
│       │   │   └── TemperatureRecordDTO.java
│       │   └── exception/
│       │       ├── BusinessException.java
│       │       └── GlobalExceptionHandler.java
│       └── resources/
│           └── application.properties
├── pom.xml
└── mvnw / mvnw.cmd
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Roadmap

- [x] Gestión completa de recetas (CRUD + búsqueda)
- [x] Ciclo de vida de lotes con validaciones de estado
- [x] Registro y monitoreo de temperatura por fase
- [x] Dashboard de producción con métricas en tiempo real
- [x] Documentación Swagger / OpenAPI
- [x] Consola H2 para inspección de datos
- [ ] Autenticación y autorización (Spring Security + JWT)
- [ ] Persistencia con PostgreSQL / MySQL en producción
- [ ] Notificaciones por email cuando un lote completa una fase
- [ ] Soporte para múltiples usuarios y roles (admin, operador)
- [ ] Frontend web para visualización del dashboard
- [ ] Tests unitarios e integración completos
- [ ] Dockerización del proyecto
- [ ] CI/CD con GitHub Actions

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Contribuir

Las contribuciones son bienvenidas y **muy apreciadas**. Si tienes una sugerencia que mejoraría el proyecto, haz un fork y crea un Pull Request, o abre un issue con la etiqueta `enhancement`.

1. Haz un **Fork** del proyecto
2. Crea tu rama de feature
   ```sh
   git checkout -b feature/nueva-funcionalidad
   ```
3. Haz commit de tus cambios
   ```sh
   git commit -m 'feat: agregar nueva funcionalidad'
   ```
4. Push a tu rama
   ```sh
   git push origin feature/nueva-funcionalidad
   ```
5. Abre un **Pull Request**

> Se recomienda seguir [Conventional Commits](https://www.conventionalcommits.org/) para los mensajes de commit.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Licencia

Distribuido bajo la Licencia MIT. Ver `LICENSE` para más información.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Contacto

**JhoanDev87**

[![GitHub][github-contact-shield]][github-contact-url]

Link del proyecto: [https://github.com/JhoanDev87/yogurt-maker](https://github.com/JhoanDev87/yogurt-maker)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- MARKDOWN LINKS & BADGES -->
[contributors-shield]: https://img.shields.io/github/contributors/JhoanDev87/yogurt-maker.svg?style=for-the-badge
[contributors-url]: https://github.com/JhoanDev87/yogurt-maker/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/JhoanDev87/yogurt-maker.svg?style=for-the-badge
[forks-url]: https://github.com/JhoanDev87/yogurt-maker/network/members
[stars-shield]: https://img.shields.io/github/stars/JhoanDev87/yogurt-maker.svg?style=for-the-badge
[stars-url]: https://github.com/JhoanDev87/yogurt-maker/stargazers
[issues-shield]: https://img.shields.io/github/issues/JhoanDev87/yogurt-maker.svg?style=for-the-badge
[issues-url]: https://github.com/JhoanDev87/yogurt-maker/issues
[license-shield]: https://img.shields.io/github/license/JhoanDev87/yogurt-maker.svg?style=for-the-badge
[license-url]: https://github.com/TU_USUARIO/TU_REPO/blob/main/LICENSE
[github-contact-shield]: https://img.shields.io/badge/GitHub-JhoanDev87-181717?style=for-the-badge&logo=github
[github-contact-url]: https://github.com/JhoanDev87

[springboot-shield]: https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[springboot-url]: https://spring.io/projects/spring-boot
[java-shield]: https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[java-url]: https://www.java.com
[h2-shield]: https://img.shields.io/badge/H2-Database-1B66C7?style=for-the-badge&logo=h2&logoColor=white
[h2-url]: https://www.h2database.com
[lombok-shield]: https://img.shields.io/badge/Lombok-Latest-BC4521?style=for-the-badge
[lombok-url]: https://projectlombok.org
[swagger-shield]: https://img.shields.io/badge/Swagger-OpenAPI_2.8.6-85EA2D?style=for-the-badge&logo=swagger&logoColor=black
[swagger-url]: https://swagger.io
[maven-shield]: https://img.shields.io/badge/Maven-Wrapper-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[maven-url]: https://maven.apache.org

