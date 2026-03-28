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

**Autenticacao e Usuarios**
- Autenticacao JWT com login e atualizacao de perfil
- Cadastro publico de usuarios (role PACIENTE)
- Painel administrativo com CRUD completo de usuarios (role ADMIN)
- Controle de acesso por roles: ADMIN, MEDICO, ENFERMEIRO, PACIENTE, EMPRESA
- Seed automatico de usuario administrador e dois enfermeiros na primeira execucao

**Vacinacao**
- Registro de vacinacoes com lote, profissional responsavel e unidade de saude
- Validacao automatica de intervalo minimo entre doses
- Prevencao de doses duplicadas (constraint paciente + vacina + dose)
- Consulta de historico vacinal completo por paciente (qualquer profissional de saude)
- Consulta de doses anteriores por vacina especifica (paciente + vacina)
- Carteira digital de vacinacao para o paciente
- Alertas de vacinas pendentes e atrasadas

**Consulta de Pacientes (profissionais)**
- Busca de pacientes por CPF ou nome (busca parcial, case-insensitive)
- Visualizacao do historico vacinal completo do paciente encontrado
- Feedback preventivo no formulario de registro: exibe doses anteriores, alerta de intervalo e esquema vacinal completo

**Gestao**
- CRUD de vacinas com tipos, doses obrigatorias e intervalos
- CRUD de unidades de saude com CNES
- Campanhas de vacinacao com metas e acompanhamento de progresso
- Dashboard com estatisticas de vacinacao em tempo real

**Empresas**
- Consulta de status vacinal de funcionarios (com consentimento do paciente)
- Gestao de consentimentos pelo paciente

**Infraestrutura**
- Migrations automaticas de banco de dados com Flyway (8 migrations)
- Documentacao interativa da API via Swagger/OpenAPI
- Deploy completo com Docker Compose (backend + frontend + banco + phpMyAdmin)

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
│   ├── entities/              #   User, Role, Vaccine, VaccinationRecord, HealthUnit, Campaign
│   ├── valueobjects/          #   Email, Login, Name, Password, UserId
│   ├── repositories/          #   Interfaces de repositorios
│   ├── usecases/              #   Interfaces e implementacoes de use cases
│   └── exceptions/            #   Excecoes de dominio
│
├── application/               # Camada de Aplicacao (orquestracao)
│   ├── services/              #   UserApplication, Auth, Vaccination, Vaccine, Campaign, HealthUnit
│   └── usecases/              #   Implementacoes de use cases
│
├── infrastructure/            # Camada de Infraestrutura (detalhes tecnicos)
│   ├── config/                #   DataSeeder, OpenApi, Repository, UseCase configs
│   ├── persistence/           #   JPA entities, mappers, repositories
│   └── security/              #   JWT, SecurityConfig, AccessTokenFilter, CORS
│
├── presentation/              # Camada de Apresentacao (interface HTTP)
│   ├── controllers/           #   Auth, User, Admin, Patient, Vaccination, Vaccine, etc.
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
│   ├── (auth)/                #   Login e Cadastro
│   └── dashboard/             #   Rotas protegidas
│       ├── page.tsx           #     Dashboard (admin/profissional)
│       ├── pacientes/         #     Busca e consulta de pacientes
│       ├── vacinacoes/        #     Registro de vacinacoes
│       ├── minha-carteira/    #     Carteira digital (paciente)
│       ├── alertas/           #     Alertas de vacinacao (paciente)
│       ├── usuarios/          #     CRUD de usuarios (admin)
│       ├── vacinas/           #     CRUD de vacinas (admin)
│       ├── unidades/          #     CRUD de unidades (admin)
│       ├── campanhas/         #     Gestao de campanhas (admin)
│       └── consultar-status/  #     Status vacinal (empresa)
│
├── components/                # Componentes reutilizaveis
│   ├── ui/                    #   Componentes Shadcn UI (base-ui)
│   ├── app-sidebar.tsx        #   Sidebar com menu por role
│   ├── auth-guard.tsx         #   Protecao de rotas autenticadas
│   ├── gov-header.tsx         #   Header padrao gov.br
│   └── gov-footer.tsx         #   Footer institucional
│
├── lib/                       # Utilitarios e configuracoes
│   ├── api.ts                 #   Cliente HTTP (48 endpoints mapeados)
│   ├── validations.ts         #   Schemas Zod
│   └── utils.ts               #   Utilitarios gerais
│
└── stores/                    # Gerenciamento de estado (Zustand)
    └── auth-store.ts          #   Estado de autenticacao + usuario
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
| OpenAPI JSON | http://localhost:8080/v3/api-docs | Especificacao OpenAPI em JSON |
| phpMyAdmin | http://localhost:8081 | Gerenciamento do banco de dados |

### 5. Login inicial

Na primeira execucao, sao criados automaticamente 3 usuarios:

| Nome | Login | Senha | Email | Role |
|---|---|---|---|---|
| Administrador | `admin` | `admin123` | admin@vaccination.system | ADMIN |
| Carlos Souza | `enf.carlos` | `enf123` | carlos.souza@vaccination.system | ENFERMEIRO |
| Ana Oliveira | `enf.ana` | `enf123` | ana.oliveira@vaccination.system | ENFERMEIRO |

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

> Documentacao interativa completa disponivel em http://localhost:8080/swagger-ui/index.html

### Autenticacao

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `POST` | `/auth/login` | Publico | Login. Retorna token JWT |
| `POST` | `/auth/update-profile` | JWT | Atualizar nome, email e/ou senha |

### Usuarios

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `POST` | `/users` | Publico | Cadastrar novo usuario (role PACIENTE) |
| `GET` | `/users/me` | JWT | Retorna perfil do usuario autenticado |

### Administracao

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `GET` | `/admin/users` | ADMIN | Listar usuarios com paginacao |
| `GET` | `/admin/users/{id}` | ADMIN | Buscar usuario por ID |
| `PUT` | `/admin/users/{id}` | ADMIN | Atualizar usuario |
| `DELETE` | `/admin/users/{id}` | ADMIN | Excluir usuario |

### Pacientes

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `GET` | `/patients/search?q=` | ENFERMEIRO, MEDICO, ADMIN | Buscar pacientes por CPF ou nome |
| `GET` | `/patients/me/vaccination-card` | PACIENTE | Carteira de vacinacao propria |
| `GET` | `/patients/me/pending-vaccines` | PACIENTE | Vacinas pendentes |
| `GET` | `/patients/me/alerts` | PACIENTE | Alertas de vacinacao |
| `GET` | `/patients/{id}/vaccination-card` | ENFERMEIRO, MEDICO, ADMIN | Carteira de vacinacao do paciente |
| `GET` | `/patients/{id}/vaccination-card/validate` | Publico | Validar carteira de vacinacao |
| `POST` | `/patients/me/consents/{companyId}` | PACIENTE | Conceder consentimento a empresa |
| `DELETE` | `/patients/me/consents/{companyId}` | PACIENTE | Revogar consentimento |
| `GET` | `/patients/me/consents` | PACIENTE | Listar consentimentos ativos |

### Vacinacoes

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `POST` | `/vaccinations` | ENFERMEIRO, MEDICO | Registrar vacinacao |
| `PUT` | `/vaccinations/{id}` | MEDICO | Editar registro de vacinacao |
| `GET` | `/vaccinations/{id}` | ENFERMEIRO, MEDICO, ADMIN | Buscar registro por ID |
| `GET` | `/vaccinations` | ENFERMEIRO, MEDICO, ADMIN | Listar todos com paginacao |
| `GET` | `/vaccinations/patient/{patientId}` | ENFERMEIRO, MEDICO, ADMIN | Historico do paciente |
| `GET` | `/vaccinations/patient/{patientId}/vaccine/{vaccineId}` | ENFERMEIRO, MEDICO, ADMIN | Doses de vacina especifica |

### Vacinas

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `GET` | `/vaccines` | JWT | Listar vacinas com paginacao |
| `GET` | `/vaccines/active` | JWT | Listar vacinas ativas |
| `GET` | `/vaccines/{id}` | JWT | Buscar vacina por ID |
| `POST` | `/vaccines` | ADMIN | Criar vacina |
| `PUT` | `/vaccines/{id}` | ADMIN | Atualizar vacina |
| `DELETE` | `/vaccines/{id}` | ADMIN | Excluir vacina |

### Unidades de Saude

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `GET` | `/health-units` | JWT | Listar unidades com paginacao |
| `GET` | `/health-units/active` | JWT | Listar unidades ativas |
| `GET` | `/health-units/{id}` | JWT | Buscar unidade por ID |
| `POST` | `/health-units` | ADMIN | Criar unidade |
| `PUT` | `/health-units/{id}` | ADMIN | Atualizar unidade |
| `DELETE` | `/health-units/{id}` | ADMIN | Excluir unidade |

### Campanhas

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `GET` | `/campaigns` | JWT | Listar campanhas com paginacao |
| `GET` | `/campaigns/active` | JWT | Listar campanhas ativas |
| `GET` | `/campaigns/{id}` | JWT | Buscar campanha por ID |
| `POST` | `/campaigns` | ADMIN | Criar campanha |
| `PUT` | `/campaigns/{id}` | ADMIN | Atualizar campanha |
| `DELETE` | `/campaigns/{id}` | ADMIN | Excluir campanha |

### Empresa

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `GET` | `/company/patients/{id}/status` | EMPRESA | Consultar status vacinal (requer consentimento) |

### Dashboard

| Metodo | Path | Auth | Descricao |
|---|---|---|---|
| `GET` | `/dashboard/stats` | ENFERMEIRO, MEDICO, ADMIN | Estatisticas gerais |
| `GET` | `/dashboard/recent-vaccinations` | ENFERMEIRO, MEDICO, ADMIN | Vacinacoes recentes |
| `GET` | `/dashboard/active-campaigns` | ENFERMEIRO, MEDICO, ADMIN | Campanhas ativas |

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
| `PACIENTE` | Visualizar carteira de vacinacao, alertas, vacinas pendentes, gerenciar consentimentos |
| `ENFERMEIRO` | Registrar vacinacoes, consultar pacientes, visualizar historico vacinal |
| `MEDICO` | Tudo do enfermeiro + editar registros de vacinacao |
| `ADMIN` | CRUD de usuarios, vacinas, unidades de saude, campanhas + dashboard |
| `EMPRESA` | Consultar status vacinal de pacientes (com consentimento) |

### Seguranca

- Senhas armazenadas com **BCrypt**
- Sessao **stateless** (sem cookies)
- Token assinado com **HMAC256**
- CORS habilitado para comunicacao frontend-backend

---

## Banco de Dados

### Diagrama ER

```
┌───────────────────┐     ┌──────────────────┐     ┌───────────────────┐
│      users        │     │    tb_role        │     │     vaccines      │
├───────────────────┤     ├──────────────────┤     ├───────────────────┤
│ id            PK  │◄─┐  │ id           PK  │     │ id            PK  │
│ name              │  │  │ name         UQ  │     │ name              │
│ email             │  │  └────────┬─────────┘     │ manufacturer      │
│ login             │  │           │               │ type              │
│ password          │  │  ┌────────┴─────────┐     │ required_doses    │
│ cpf           UQ  │  │  │  tb_user_role    │     │ dose_interval_days│
│ birth_date        │  ├──│ user_id      FK  │     │ active            │
│ sex               │  │  │ role_id      FK  │     └────────┬──────────┘
└──────┬────────────┘  │  └──────────────────┘              │
       │               │                                    │
       │  ┌────────────┴──────────────────┐                 │
       │  │    vaccination_records        │                 │
       │  ├───────────────────────────────┤                 │
       │  │ id                        PK  │                 │
       ├──│ patient_id                FK  │◄────────────────┘
       ├──│ professional_id           FK  │  (vaccine_id FK)
       │  │ vaccine_id                FK  │
       │  │ health_unit_id            FK  ├──┐
       │  │ dose_number                   │  │  ┌──────────────────┐
       │  │ lot_number                    │  │  │  health_units    │
       │  │ application_date              │  │  ├──────────────────┤
       │  │ notes                         │  └─►│ id           PK  │
       │  │ UQ(patient, vaccine, dose)    │     │ name             │
       │  └───────────────────────────────┘     │ cnes             │
       │                                        │ address          │
       │  ┌───────────────────────────────┐     │ active           │
       │  │    patient_consents           │     └──────────────────┘
       │  ├───────────────────────────────┤
       ├──│ patient_id                FK  │     ┌──────────────────┐
       └──│ company_id                FK  │     │    campaigns     │
          │ granted                       │     ├──────────────────┤
          │ granted_at                    │     │ id           PK  │
          │ revoked_at                    │     │ name             │
          └───────────────────────────────┘     │ vaccine_id   FK  │
                                                │ dose_goal        │
                                                │ start/end_date   │
                                                │ active           │
                                                └──────────────────┘
```

### Migrations (Flyway)

| Versao | Arquivo | Descricao |
|---|---|---|
| V1 | `V1__Create_users_table.sql` | Tabela de usuarios |
| V2 | `V2__Create_roles_table.sql` | Tabelas de roles e user_role + seed USER/ADMIN |
| V3 | `V3__Add_patient_fields_and_roles.sql` | Roles PACIENTE, ENFERMEIRO, MEDICO, EMPRESA + campos CPF, birth_date, sex |
| V4 | `V4__Create_vaccines_table.sql` | Tabela de vacinas |
| V5 | `V5__Create_health_units_table.sql` | Tabela de unidades de saude |
| V6 | `V6__Create_vaccination_records_table.sql` | Registros de vacinacao com constraint de unicidade |
| V7 | `V7__Create_campaigns_table.sql` | Campanhas de vacinacao |
| V8 | `V8__Create_patient_consents_table.sql` | Consentimentos paciente-empresa |

### Seed Automatico

Ao iniciar a aplicacao, o `DataSeeder` verifica se os usuarios iniciais existem. Caso nao existam, cria automaticamente:

| Nome | Login | Senha | Email | Role |
|---|---|---|---|---|
| Administrador | `admin` | `admin123` | admin@vaccination.system | ADMIN |
| Carlos Souza | `enf.carlos` | `enf123` | carlos.souza@vaccination.system | ENFERMEIRO |
| Ana Oliveira | `enf.ana` | `enf123` | ana.oliveira@vaccination.system | ENFERMEIRO |

---

## Telas do Frontend

### Publicas

| Rota | Descricao |
|---|---|
| `/login` | Autenticacao com validacao via Zod. Header/footer no padrao gov.br |
| `/cadastro` | Formulario de criacao de conta com validacao completa |
| `/validar/{id}` | Validacao publica de carteira de vacinacao via QR Code |

### Paciente

| Rota | Descricao |
|---|---|
| `/dashboard/minha-carteira` | Carteira digital de vacinacao com todas as doses recebidas |
| `/dashboard/alertas` | Alertas de vacinas pendentes e atrasadas |

### Profissionais (Enfermeiro / Medico)

| Rota | Descricao |
|---|---|
| `/dashboard` | Painel com estatisticas, vacinacoes recentes e campanhas ativas |
| `/dashboard/pacientes` | **Busca de pacientes** por CPF ou nome com visualizacao do historico vacinal completo |
| `/dashboard/vacinacoes` | Registro de vacinacoes com **exibicao de doses anteriores**, alerta de intervalo minimo e auto-preenchimento do numero da dose |

### Administrador

| Rota | Descricao |
|---|---|
| `/dashboard/usuarios` | CRUD completo de usuarios com paginacao |
| `/dashboard/vacinas` | CRUD de vacinas (tipos, doses, intervalos) |
| `/dashboard/unidades` | CRUD de unidades de saude |
| `/dashboard/campanhas` | Gestao de campanhas de vacinacao com progresso |

### Empresa

| Rota | Descricao |
|---|---|
| `/dashboard/consultar-status` | Consulta de status vacinal de funcionarios (com consentimento) |

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
