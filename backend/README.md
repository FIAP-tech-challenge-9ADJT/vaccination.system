# Vaccination System - API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Docker](https://img.shields.io/badge/Docker-✓-2496ED)

## Visao Geral

API REST para o **Sistema de Gerenciamento de Vacinacao**, desenvolvida com **Spring Boot 3.5.3** e **Java 21**.

### Funcionalidades

- Autenticacao JWT (login, atualizacao de perfil)
- Gestao de usuarios (cadastro, consulta, exclusao)
- Controle de acesso por roles (USER, ADMIN)
- Seed automatico de admin ao subir via Docker
- Migrations automaticas com Flyway
- Documentacao OpenAPI/Swagger

---

## Arquitetura

```
presentation/     -> Controllers, DTOs, Mappers (REST API)
application/      -> Use Cases, Application Services
domain/           -> Entities, Value Objects, Repository Interfaces
infrastructure/   -> Database, Security, Configurations
```

---

## Endpoints da API

### Autenticacao

| Metodo | Path                  | Auth     | Descricao                    |
|--------|-----------------------|----------|------------------------------|
| POST   | `/auth/login`         | Publico  | Login, retorna token JWT     |
| POST   | `/auth/update-profile`| JWT      | Atualizar perfil/senha       |

### Usuarios

| Metodo | Path          | Auth     | Descricao                    |
|--------|---------------|----------|------------------------------|
| POST   | `/users`      | Publico  | Criar usuario (role USER)    |
| GET    | `/users/me`   | JWT      | Perfil do usuario autenticado|
| GET    | `/users/{id}` | ADMIN    | Buscar usuario por ID        |
| DELETE | `/users/{id}` | ADMIN    | Deletar usuario              |

### Admin

| Metodo | Path                | Auth  | Descricao              |
|--------|---------------------|-------|------------------------|
| GET    | `/admin/users/{id}` | ADMIN | Buscar usuario por ID  |
| DELETE | `/admin/users/{id}` | ADMIN | Deletar usuario        |

### Documentacao Interativa

```
http://localhost:8080/swagger-ui/index.html
```

---

## Configuracao e Deploy

### Pre-requisitos

- **Docker** e **Docker Compose**
- **Java 21** (para desenvolvimento local)
- **Maven** (para build local)

### Variaveis de Ambiente

```bash
cp .env.example .env
```

```env
MYSQL_DATABASE=vaccination_system
MYSQL_ROOT_PASSWORD=your_secure_root_password_here
```

### Deploy com Docker

```bash
# Subir os containers
docker-compose up -d --build

# Parar os containers
docker-compose down
```

Servicos disponveis:
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui/index.html
- **phpMyAdmin**: http://localhost:8081

### Deploy Local (sem Docker)

```bash
# Configurar MySQL local na porta 3306
# Criar arquivo .env com as variaveis

mvn spring-boot:run
```

---

## Seed do Admin

Ao iniciar a aplicacao, um usuario admin e criado automaticamente caso nao exista:

| Campo    | Valor                       |
|----------|-----------------------------|
| Login    | `admin`                     |
| Senha    | `admin123`                  |
| Email    | `admin@vaccination.system`  |
| Role     | `ADMIN`                     |

---

## Seguranca

- **JWT**: Token Bearer com expiracao de 30 minutos
- **BCrypt**: Hash de senhas
- **Sessao Stateless**: Sem cookies
- **Roles**: `USER` e `ADMIN`

Header de autenticacao:
```
Authorization: Bearer <token>
```

---

## Banco de Dados

### Schema

**users**
```sql
id          BIGINT PRIMARY KEY AUTO_INCREMENT
name        VARCHAR(100)
email       VARCHAR(100)
login       VARCHAR(50)
password    VARCHAR(255)
created_at  DATETIME
updated_at  DATETIME
```

**tb_role**
```sql
id    BIGINT PRIMARY KEY AUTO_INCREMENT
name  VARCHAR(50) UNIQUE -- USER, ADMIN
```

**tb_user_role**
```sql
user_id  BIGINT (FK -> users)
role_id  BIGINT (FK -> tb_role)
```

---

## Comandos Uteis

```bash
# Executar testes
mvn test

# Build do projeto
mvn clean package

# Build sem testes
mvn clean package -DskipTests

# Executar localmente
mvn spring-boot:run
```

---

## Estrutura do Projeto

```
backend/
├── src/main/java/tech/challenge/vaccination/system/
│   ├── domain/
│   │   ├── entities/          # User, Role
│   │   ├── exceptions/        # Domain exceptions
│   │   ├── repositories/      # Repository interfaces
│   │   ├── usecases/          # Use case interfaces
│   │   └── valueobjects/      # Email, Login, Name, Password, UserId
│   ├── application/
│   │   ├── services/          # UserApplicationService, AuthApplicationService
│   │   └── usecases/          # Use case implementations
│   ├── infrastructure/
│   │   ├── config/            # DataSeeder, OpenApi, Repository, UseCase configs
│   │   ├── persistence/       # JPA entities, mappers, repositories
│   │   └── security/          # JWT, SecurityConfig, AccessTokenFilter
│   ├── presentation/
│   │   ├── controllers/       # AuthController, UserController, AdminController
│   │   ├── dtos/              # Request/Response DTOs
│   │   └── mappers/           # DTO mappers
│   ├── exceptions/            # Exception handlers
│   └── validations/           # Custom validators
├── src/main/resources/
│   ├── application.properties
│   ├── application-docker.properties
│   └── db/migration/          # Flyway migrations
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```
