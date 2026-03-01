# 📦 Sistema de Inventario

Sistema web completo para gestión de inventario, facturación y control de stock, desarrollado con **Spring Boot 3** y **Angular 21**.

---

## 🛠️ Tech Stack

### Backend

- **Java 22** + **Spring Boot 3.4.5**
- **Spring Security** + **JWT** (autenticación y autorización)
- **Spring Data JPA** + **Hibernate 6**
- **MySQL 8.0**
- **Lombok** (reducción de boilerplate)
- **Bean Validation** (jakarta.validation)

### Frontend

- **Angular 21** (standalone components, signals)
- **Lucide Icons** (iconografía SVG profesional)
- **CSS Variables** + **DM Sans** (diseño moderno y responsive)

---

## 📋 Funcionalidades

- **Autenticación JWT** — Login, registro, roles (ADMIN / USER), auto-logout por token expirado
- **Dashboard** — Resumen con totales de productos, clientes, proveedores y facturas
- **Productos** — CRUD completo con categoría y proveedor asociados
- **Categorías** — Gestión de categorías de productos
- **Clientes** — Registro y administración de clientes
- **Proveedores** — Gestión de proveedores
- **Facturas** — Creación de facturas con múltiples items, cálculo automático de totales
- **Movimientos** — Entradas y salidas de inventario con actualización automática de stock
- **Control de stock** — Badges visuales para stock bajo (≤ 5 unidades)

---

## 🏗️ Arquitectura Backend

```
src/main/java/com/cantero/sistema_inventario/
├── config/          → Configuración CORS
├── controller/      → 10 REST Controllers
├── dto/             → LoginRequest, LoginResponse, RegisterRequest
├── model/           → 9 Entidades JPA (Producto, Factura, Movimiento, etc.)
├── repository/      → 8 JPA Repositories
├── security/
│   ├── config/      → SecurityConfig (filtros, rutas protegidas)
│   ├── jwt/         → JwtTokenProvider, JwtAuthenticationFilter
│   └── service/     → AuthService, CustomUserDetailsService
└── service/         → 9 Service classes (lógica de negocio)
```

---

## ⚙️ Configuración y ejecución

### Prerrequisitos

- Java 22+
- MySQL 8.0+
- Node.js 18+ y Angular CLI

### 1. Base de datos

```sql
CREATE DATABASE inventario_db;
```

### 2. Backend

```bash
cd sistema-inventario-back
```

Configura las variables de entorno (o edita `application.properties`):

| Variable       | Descripción                      | Default              |
|----------------|----------------------------------|----------------------|
| `DB_USERNAME`  | Usuario MySQL                    | `root`               |
| `DB_PASSWORD`  | Contraseña MySQL                 | *(vacío)*            |
| `JWT_SECRET`   | Clave secreta para firmar tokens | `defaultDevSecret...`|

**Opción A — Variables de entorno:**

```bash
set DB_USERNAME=root
set DB_PASSWORD=tu_password
set JWT_SECRET=TuClaveSecretaSegura123456789
```

**Opción B — IntelliJ IDEA:**

Run → Edit Configurations → Environment variables:

```
DB_USERNAME=root;DB_PASSWORD=tu_password;JWT_SECRET=TuClaveSecretaSegura123456789
```

Ejecutar:

```bash
./mvnw spring-boot:run
```

El backend estará en `http://localhost:8080`

### 3. Frontend

```bash
cd sistema-inventario-front
npm install
ng serve
```

La aplicación estará en `http://localhost:4200`

---

## 🔐 Seguridad

- Autenticación basada en **JWT** con expiración configurable (default: 24h)
- Contraseñas encriptadas con **BCrypt**
- Endpoints protegidos por roles (`ADMIN`, `USER`)
- Interceptor HTTP que detecta tokens expirados (401) y redirige al login
- Campo `password` excluido de respuestas JSON con `@JsonIgnore`

---

## 📡 API Endpoints principales

| Método                    | Endpoint               | Descripción                  |
|---------------------------|------------------------|------------------------------|
| POST                      | `/api/auth/login`      | Iniciar sesión               |
| POST                      | `/api/auth/register`   | Registrar usuario            |
| GET                       | `/api/dashboard/totales` | Totales del dashboard      |
| GET/POST/PUT/DELETE       | `/api/productos`       | CRUD Productos               |
| GET/POST/PUT/DELETE       | `/api/categorias`      | CRUD Categorías              |
| GET/POST/PUT/DELETE       | `/api/clientes`        | CRUD Clientes                |
| GET/POST/PUT/DELETE       | `/api/proveedores`     | CRUD Proveedores             |
| GET/POST                  | `/api/facturas`        | Gestión de facturas          |
| GET/POST                  | `/api/movimientos`     | Movimientos de inventario    |
| GET                       | `/api/usuarios`        | Listar usuarios (ADMIN)      |

---

## 📱 Responsive

La interfaz se adapta a desktop, tablet y móvil:

- **Sidebar** colapsable con menú hamburguesa en móvil
- **Tablas** con scroll horizontal
- **Modales** adaptados a pantallas pequeñas
- **Dashboard** con grid responsive (4 → 2 → 1 columnas)

---

## 👤 Autor

**Jesús Cantero** — Desarrollador en formación  
Tecnólogo en Análisis y Desarrollo de Software — SENA

---

## 📄 Licencia

Este proyecto es de uso educativo y personal.