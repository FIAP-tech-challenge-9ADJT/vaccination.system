# Vaccination System

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-6DB33F?logo=spring-boot&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-16-000000?logo=next.js&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind-CSS-06B6D4?logo=tailwindcss&logoColor=white)

Proposta de **Sistema Nacional de Vacinacao** desenvolvida para o Hackathon FIAP 2026. A aplicacao gerencia o cadastro de usuarios, autenticacao e serve como base para um sistema completo de controle de vacinacao com historico de vacinas do paciente.

O frontend segue o padrao visual do **Design System do Governo Federal (DSGov)**, utilizando a paleta de cores, tipografia e padroes de comunicacao oficiais do gov.br.

---

## Indice

- [Visao Geral](#visao-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Inicio Rapido](#inicio-rapido)
- [Desenvolvimento Local](#desenvolvimento-local)
- [Endpoints da API](#endpoints-da-api)
- [Autenticacao](#autenticacao)
- [Banco de Dados](#banco-de-dados)
- [Telas do Frontend](#telas-do-frontend)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Docker](#docker)
- [Variaveis de Ambiente](#variaveis-de-ambiente)
- [Comandos Uteis](#comandos-uteis)

---

## Visao Geral

### Funcionalidades Implementadas

- Autenticacao JWT com login e atualizacao de perfil
- Cadastro publico de usuarios (role USER)
- Painel administrativo com CRUD completo de usuarios (role ADMIN)
- Dashboard com indicadores de vacinacao (dados de exemplo)
- Listagem paginada de usuarios
- Seed automatico de usuario administrador
- Migrations automaticas de banco de dados com Flyway
- Documentacao interativa da API via Swagger/OpenAPI
- Deploy completo com Docker Compose (backend + frontend + banco + phpMyAdmin)

### Proposta do Hackathon

O sistema foi projetado como base extensivel para um sistema nacional de vacinacao que incluira:

- Cadastro de pacientes com CPF e dados demograficos
- Registro de vacinas aplicadas com lote, fabricante e profissional responsavel
- Historico completo de vacinacao por paciente
- Carteira digital de vacinacao
- Campanhas de vacinacao com metas e acompanhamento
- Agendamento de doses
- Relatorios de cobertura vacinal

---

## Tecnologias

### Backend

| Tecnologia | Versao | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.3 | Framework web |
| Spring Security | 6.x | Autenticacao e autorizacao |
| Spring Data JPA | 3.x | Persistencia de dados |
| MySQL | 8.0 | Banco de dados relacional |
| Flyway | 10.x | Migrations de banco de dados |
| Auth0 java-jwt | 4.4.0 | Geracao e validacao de tokens JWT |
| SpringDoc OpenAPI | 2.8.7 | Documentacao Swagger |
| Lombok | 1.18.32 | Reducao de boilerplate |
| JaCoCo | 0.8.11 | Cobertura de testes (minimo 80%) |

### Frontend

| Tecnologia | Versao | Finalidade |
|---|---|---|
| Next.js | 16.2.1 | Framework React com App Router |
| React | 19.2.4 | Biblioteca de UI |
| TypeScript | 5.x | Tipagem estatica |
| Tailwind CSS | 4.x | Estilizacao utilitaria |
| Shadcn UI | 4.x | Componentes de interface (base-ui) |
| Zustand | 5.x | Gerenciamento de estado |
| Zod | 4.x | Validacao de schemas |
| React Hook Form | 7.x | Gerenciamento de formularios |
| Lucide React | 1.x | Icones |

### Infraestrutura

| Tecnologia | Finalidade |
|---|---|
| Docker + Docker Compose | Orquestracao de containers |
| phpMyAdmin | Interface grafica para banco de dados |

---

## Arquitetura

### Visao Geral

```
                    ┌─────────────┐
                    │   Browser   │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            ▼            ▼
       ┌────────────┐ ┌────────┐ ┌──────────┐
       │  Frontend   │ │Backend │ │phpMyAdmin│
       │  Next.js    │ │Spring  │ │          │
       │  :3000      │ │:8080   │ │:8081     │
       └────────────┘ └───┬────┘ └────┬─────┘
                          │           │
                          ▼           ▼
                    ┌─────────────────────┐
                    │     MySQL 8.0       │
                    │     :3306           │
                    └─────────────────────┘
```

### Backend - Clean Architecture

O backend segue os principios de Clean Architecture com separacao clara entre camadas:

```
backend/src/main/java/tech/challenge/vaccination/system/
│
├── domain/                    # Camada de Dominio (regras de negocio)
│   ├── entities/              #   User, Role
│   ├── valueobjects/          #   Email, Login, Name, Password, UserId
│   ├── repositories/          #   Interfaces de repositorios
│   ├── usecases/              #   Interfaces e implementacoes de use cases
│   └── exceptions/            #   Excecoes de dominio
│
├── application/               # Camada de Aplicacao (orquestracao)
│   ├── services/              #   UserApplicationService, AuthApplicationService
│   └── usecases/              #   Implementacoes de use cases
│
├── infrastructure/            # Camada de Infraestrutura (detalhes tecnicos)
│   ├── config/                #   DataSeeder, OpenApi, Repository, UseCase configs
│   ├── persistence/           #   JPA entities, mappers, repositories
│   └── security/              #   JWT, SecurityConfig, AccessTokenFilter, CORS
│
├── presentation/              # Camada de Apresentacao (interface HTTP)
│   ├── controllers/           #   AuthController, UserController, AdminController
│   ├── dtos/                  #   Request/Response DTOs
│   └── mappers/               #   Conversao Entity <-> DTO
│
├── exceptions/                # Exception handlers globais
└── validations/               # Validadores customizados (@UniqueEmail, @ValidCPF, etc.)
```

**Fluxo de uma requisicao:**

```
HTTP Request → Controller → Application Service → Use Case → Repository → Database
HTTP Response ← DTO Mapper ← Domain Entity ← ← ← ← ← ← ← ← ← ← ← ← ←
```

### Frontend - Next.js App Router

```
frontend/src/
│
├── app/                       # Rotas da aplicacao (App Router)
│   ├── (auth)/                #   Grupo de rotas de autenticacao
│   │   ├── login/             #     Tela de login
│   │   └── cadastro/          #     Tela de cadastro
│   └── dashboard/             #   Grupo de rotas protegidas
│       ├── page.tsx           #     Dashboard principal
│       └── usuarios/          #     CRUD de usuarios
│
├── components/                # Componentes reutilizaveis
│   ├── ui/                    #   Componentes Shadcn UI
│   ├── app-sidebar.tsx        #   Sidebar de navegacao
│   ├── auth-guard.tsx         #   Protecao de rotas autenticadas
│   ├── gov-header.tsx         #   Header padrao gov.br
│   └── gov-footer.tsx         #   Footer institucional
│
├── lib/                       # Utilitarios e configuracoes
│   ├── api.ts                 #   Cliente HTTP para o backend
│   ├── validations.ts         #   Schemas Zod
│   └── utils.ts               #   Utilitarios gerais
│
└── stores/                    # Gerenciamento de estado (Zustand)
    ├── auth-store.ts          #   Estado de autenticacao
    └── users-store.ts         #   Estado de usuarios
```

---

## Inicio Rapido

### Pre-requisitos

- [Docker](https://docs.docker.com/get-docker/) e [Docker Compose](https://docs.docker.com/compose/install/)

### 1. Clonar o repositorio

```bash
git clone <url-do-repositorio>
cd vaccination.system
```

### 2. Configurar variaveis de ambiente

```bash
cp .env.example .env
```

Edite o arquivo `.env` conforme necessario:

```env
MYSQL_DATABASE=vaccination_system
MYSQL_ROOT_PASSWORD=sua_senha_segura
NEXT_PUBLIC_API_URL=http://localhost:8080
```

### 3. Subir a aplicacao

```bash
docker-compose up -d --build
```

### 4. Acessar os servicos

| Servico | URL | Descricao |
|---|---|---|
| Frontend | http://localhost:3000 | Interface web da aplicacao |
| Backend API | http://localhost:8080 | API REST |
| Swagger UI | http://localhost:8080/swagger-ui/index.html | Documentacao interativa da API |
| phpMyAdmin | http://localhost:8081 | Gerenciamento do banco de dados |

### 5. Login inicial

Um usuario administrador e criado automaticamente na primeira execucao:

| Campo | Valor |
|---|---|
| Login | `admin` |
| Senha | `admin123` |
| Email | `admin@vaccination.system` |
| Role | `ADMIN` |

---

## Desenvolvimento Local

### Backend (sem Docker)

**Pre-requisitos:** Java 21, Maven, MySQL 8.0 rodando na porta 3306

```bash
cd backend

# Criar arquivo .env com as variaveis de banco
cp .env.example .env

# Executar a aplicacao
./mvnw spring-boot:run
```

### Frontend (sem Docker)

**Pre-requisitos:** Node.js 22+

```bash
cd frontend

# Instalar dependencias
npm install

# Executar em modo de desenvolvimento
npm run dev
```

O frontend estara disponivel em http://localhost:3000 e se conectara ao backend em http://localhost:8080 (configuravel via `NEXT_PUBLIC_API_URL`).

---

## Endpoints da API

### Autenticacao

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `POST` | `/auth/login` | Publico | Login. Retorna token JWT |
| `POST` | `/auth/update-profile` | JWT (USER, ADMIN) | Atualizar nome, email e/ou senha |

**Exemplo - Login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login": "admin", "password": "admin123"}'
```

**Resposta:**

```json
{
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGc..."
}
```

### Usuarios

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `POST` | `/users` | Publico | Cadastrar novo usuario (role USER) |
| `GET` | `/users/me` | JWT (USER, ADMIN) | Retorna perfil do usuario autenticado |

**Exemplo - Cadastrar usuario:**

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Silva",
    "email": "maria@email.com",
    "login": "maria_silva",
    "password": "senha123"
  }'
```

**Resposta (201 Created):**

```json
{
  "id": 2,
  "name": "Maria Silva",
  "email": "maria@email.com",
  "login": "maria_silva",
  "createdAt": "2026-03-23T14:30:00",
  "updatedAt": "2026-03-23T14:30:00",
  "roles": [
    { "id": 1, "name": "USER" }
  ]
}
```

### Administracao (requer role ADMIN)

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `GET` | `/admin/users?page=0&size=10` | ADMIN | Listar todos os usuarios com paginacao |
| `GET` | `/admin/users/{id}` | ADMIN | Buscar usuario por ID |
| `PUT` | `/admin/users/{id}` | ADMIN | Atualizar nome e email de um usuario |
| `DELETE` | `/admin/users/{id}` | ADMIN | Excluir usuario |

**Exemplo - Listar usuarios (com token):**

```bash
curl http://localhost:8080/admin/users?page=0&size=10 \
  -H "Authorization: Bearer <TOKEN>"
```

**Resposta:**

```json
{
  "content": [
    {
      "id": 1,
      "name": "Administrador",
      "email": "admin@vaccination.system",
      "login": "admin",
      "createdAt": "2026-03-23T14:00:00",
      "updatedAt": "2026-03-23T14:00:00",
      "roles": [{ "id": 2, "name": "ADMIN" }]
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

**Exemplo - Atualizar usuario:**

```bash
curl -X PUT http://localhost:8080/admin/users/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"name": "Maria Santos", "email": "maria.santos@email.com"}'
```

---

## Autenticacao

O sistema utiliza **JWT (JSON Web Tokens)** para autenticacao stateless.

### Fluxo

1. O cliente envia `POST /auth/login` com login e senha
2. O backend valida as credenciais e retorna um `accessToken`
3. O cliente inclui o token no header de todas as requisicoes protegidas:
   ```
   Authorization: Bearer <accessToken>
   ```
4. O token expira em **30 minutos**

### Roles

| Role | Permissoes |
|---|---|
| `USER` | Visualizar proprio perfil, atualizar perfil |
| `ADMIN` | Todas as permissoes de USER + CRUD completo de usuarios |

### Seguranca

- Senhas armazenadas com **BCrypt**
- Sessao **stateless** (sem cookies)
- Token assinado com **HMAC256**
- CORS habilitado para comunicacao frontend-backend

---

## Banco de Dados

### Diagrama ER

```
┌──────────────────────┐       ┌──────────────────┐
│        users         │       │     tb_role      │
├──────────────────────┤       ├──────────────────┤
│ id         BIGINT PK │◄──┐   │ id    BIGINT PK  │
│ name       VARCHAR   │   │   │ name  VARCHAR UQ  │
│ email      VARCHAR   │   │   │  (USER, ADMIN)   │
│ login      VARCHAR   │   │   └────────┬─────────┘
│ password   VARCHAR   │   │            │
│ created_at DATETIME  │   │   ┌────────┴─────────┐
│ updated_at DATETIME  │   │   │  tb_user_role    │
└──────────────────────┘   │   ├──────────────────┤
                           ├───│ user_id BIGINT FK│
                               │ role_id BIGINT FK│
                               └──────────────────┘
```

### Migrations (Flyway)

| Versao | Arquivo | Descricao |
|---|---|---|
| V1 | `V1__Create_users_table.sql` | Tabela de usuarios |
| V2 | `V2__Create_roles_table.sql` | Tabelas de roles e user_role + seed USER/ADMIN |

### Seed Automatico

Ao iniciar a aplicacao, o `DataSeeder` verifica se o usuario `admin` existe. Caso nao exista, cria automaticamente:

```
Login: admin
Senha: admin123
Email: admin@vaccination.system
Role:  ADMIN
```

---

## Telas do Frontend

### Login (`/login`)

Tela de autenticacao com validacao de formulario via Zod. Header e footer seguem o padrao visual gov.br com barra amarela superior.

### Cadastro (`/cadastro`)

Formulario de criacao de conta com validacao de nome, email, login, senha e confirmacao de senha.

### Dashboard (`/dashboard`)

Painel com indicadores de exemplo:
- Pacientes cadastrados, vacinas aplicadas, cobertura vacinal, agendamentos
- Lista de vacinacoes recentes com status
- Campanhas de vacinacao em andamento com barra de progresso

### Usuarios (`/dashboard/usuarios`)

CRUD completo de usuarios com:
- Listagem paginada em tabela
- Botao "Novo Usuario" abrindo dialog de cadastro
- Botao de edicao (lapiz) abrindo dialog de edicao de nome/email
- Botao de exclusao com confirmacao via AlertDialog
- Badges coloridos por role (ADMIN em azul, USER em cinza)

---

## Estrutura do Projeto

```
vaccination.system/
├── .env.example                # Variaveis de ambiente (template)
├── .gitignore                  # Arquivos ignorados pelo git
├── docker-compose.yml          # Orquestracao de todos os servicos
├── README.md                   # Esta documentacao
│
├── backend/                    # API REST (Spring Boot)
│   ├── .dockerignore
│   ├── .env.example
│   ├── Dockerfile              # Multi-stage build (Maven + JRE Alpine)
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/
│       └── main/
│           ├── java/tech/challenge/vaccination/system/
│           │   ├── domain/           # Entidades, Value Objects, Repositorios
│           │   ├── application/      # Services e Use Cases
│           │   ├── infrastructure/   # JPA, Security, Configs
│           │   ├── presentation/     # Controllers, DTOs, Mappers
│           │   ├── exceptions/       # Exception handlers
│           │   └── validations/      # Validadores customizados
│           └── resources/
│               ├── application.properties
│               ├── application-docker.properties
│               └── db/migration/     # Flyway SQL migrations
│
└── frontend/                   # Interface Web (Next.js)
    ├── .dockerignore
    ├── Dockerfile              # Multi-stage build (Node + standalone)
    ├── package.json
    ├── next.config.ts
    └── src/
        ├── app/                # Rotas (App Router)
        │   ├── (auth)/         #   Login e Cadastro
        │   └── dashboard/      #   Dashboard e Usuarios
        ├── components/         # Componentes reutilizaveis
        ├── lib/                # API client, validacoes, utils
        └── stores/             # Estado global (Zustand)
```

---

## Docker

### Servicos

| Servico | Imagem | Porta | Descricao |
|---|---|---|---|
| `mysql` | mysql:8.0 | 3306 | Banco de dados com health check e volume persistente |
| `backend` | Build local (Alpine JRE 21) | 8080 | API REST, aguarda MySQL ficar saudavel |
| `frontend` | Build local (Node 22 Alpine) | 3000 | Interface web, output standalone |
| `phpmyadmin` | phpmyadmin/phpmyadmin | 8081 | Interface grafica para o banco |

### Otimizacoes Aplicadas

- **Multi-stage builds** em backend e frontend para imagens menores
- **Imagens Alpine** para reducao de tamanho
- **Cache de dependencias** separado do codigo fonte (melhor cache de layers)
- **Usuarios nao-root** nos containers de aplicacao
- **Health checks** em MySQL, backend e frontend
- **Volume nomeado** para persistencia dos dados do MySQL
- **`.dockerignore`** em ambos os projetos para builds mais rapidos
- **Next.js standalone** output para imagem frontend otimizada (~150MB)

### Comandos

```bash
# Subir todos os servicos
docker-compose up -d --build

# Ver logs de todos os servicos
docker-compose logs -f

# Ver logs de um servico especifico
docker-compose logs -f backend

# Parar todos os servicos
docker-compose down

# Parar e remover volumes (apaga dados do banco)
docker-compose down -v

# Rebuild de um servico especifico
docker-compose up -d --build backend
```

---

## Variaveis de Ambiente

### Arquivo `.env` (raiz do projeto)

| Variavel | Obrigatoria | Padrao | Descricao |
|---|---|---|---|
| `MYSQL_DATABASE` | Sim | - | Nome do banco de dados |
| `MYSQL_ROOT_PASSWORD` | Sim | - | Senha root do MySQL |
| `NEXT_PUBLIC_API_URL` | Nao | `http://localhost:8080` | URL do backend acessada pelo browser |

### Arquivo `.env` (backend)

| Variavel | Obrigatoria | Descricao |
|---|---|---|
| `MYSQL_DATABASE` | Sim | Nome do banco de dados |
| `MYSQL_USER` | Sim | Usuario do banco |
| `MYSQL_PASSWORD` | Sim | Senha do banco |
| `MYSQL_ROOT_PASSWORD` | Sim | Senha root (usado no Docker) |

---

## Comandos Uteis

### Backend

```bash
cd backend

# Executar localmente
./mvnw spring-boot:run

# Build do projeto
./mvnw clean package

# Build sem testes
./mvnw clean package -DskipTests

# Executar testes
./mvnw test

# Gerar relatorio de cobertura
./mvnw test jacoco:report
# Relatorio em: target/site/jacoco/index.html
```

### Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Executar em modo desenvolvimento
npm run dev

# Build de producao
npm run build

# Executar build de producao
npm start

# Lint
npm run lint
```

### Docker

```bash
# Subir tudo
docker-compose up -d --build

# Verificar status dos containers
docker-compose ps

# Acessar shell do container backend
docker-compose exec backend sh

# Acessar MySQL via CLI
docker-compose exec mysql mysql -uroot -p vaccination_system
```

---

## Design System

O frontend segue o **Padrao Digital de Governo (DSGov)** com as seguintes cores principais:

| Token | Cor | Hex | Uso |
|---|---|---|---|
| `gov-blue` | Azul Institucional | `#1351B4` | Botoes primarios, links, destaques |
| `gov-blue-dark` | Azul Escuro | `#071D41` | Header, sidebar, titulos |
| `gov-green` | Verde | `#168821` | Sucesso, botoes de acao positiva |
| `gov-yellow` | Amarelo | `#FFCD07` | Barra superior, foco |
| `gov-red` | Vermelho | `#E52207` | Erros, acoes destrutivas |

Comunicacao em tom semi-formal, linguagem clara e acessivel, conforme a **Lei 15.263/2025** (Politica Nacional de Linguagem Simples).

---

## Licenca

MIT License
