# FutStats Manager — Backend

API REST Spring Boot do **FutStats Manager**.

## Stack

- Java 17
- Spring Boot 3.4.6
- Spring Data JPA + Hibernate
- Spring Security + JWT (JJWT 0.12)
- PostgreSQL / H2

## Executar

```bash
mvn spring-boot:run
```

Porta padrão: **8080**.

### Profiles

Definidos via `APP_PROFILE` (padrão: `local`):

| Profile | Arquivo | Banco |
|---------|---------|-------|
| `local` | `application-local.properties` | PostgreSQL |
| `local-h2` | `application-local-h2.properties` | H2 em arquivo |
| `dev` | `application-dev.properties` | PostgreSQL remoto |
| `test` | `application-test.properties` | H2 em memória |

### Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `APP_PROFILE` | Profile Spring ativo |
| `APP_JWT_SECRET` | Secret JWT (HS256, ≥32 chars) |
| `APP_JWT_EXPIRATION_MS` | Expiração do token (ms) |

Datasource configurado por profile. Para PostgreSQL local, editar `application-local.properties` ou usar variáveis Spring padrão (`SPRING_DATASOURCE_URL`, etc.).

## Build e testes

```bash
mvn clean package          # build + testes
mvn test                   # apenas testes
mvn spring-boot:run -Dspring-boot.run.profiles=local-h2  # sem PostgreSQL
```

**27 testes** cobrindo: contexto Spring, sistema disciplinar (14), recálculo disciplinar (5), política de evolução de rank (7).

## Estrutura

```
src/main/java/br/com/bellato/gerenciador_fifa/
├── controller/     # REST endpoints
├── service/        # Lógica de negócio
├── repository/     # JPA repositories
├── model/          # Entidades
├── dto/            # Request/Response
├── mapper/         # Conversões entity ↔ DTO
├── validator/      # Validações de campeonato e partida
├── enums/          # Status, ranks, eventos
├── config/         # Security, CORS, migrators
├── security/       # JWT filter
└── exception/      # Handler global
```

## Endpoints principais

| Prefixo | Controller | Domínio |
|---------|------------|---------|
| `/auth` | `AuthController` | Login, registro |
| `/api/atletas` | `AtletaController` | CRUD atletas globais |
| `/api/clubes` | `ClubeController` | CRUD clubes globais |
| `/api/estatisticas` | `EstatisticaAtletaController` | Estatísticas de atletas |
| `/api/campeonato` | `CampeonatoController` | Campeonatos, partidas, mercado, motor |
| `/api/hall` | `HallDaFamaController` | Recordes, rankings, busca |
| `/api/usuarios` | `UserController` | Perfil, rivalidades, busca |
| `/api/enums` | `EnumController` | Enums públicos |

## Banco de dados

- Desenvolvimento: `spring.jpa.hibernate.ddl-auto=update`.
- Migrations SQL em `src/main/resources/db/migration/` (V1–V12) — documentação histórica; Flyway **não está ativo**.
- Migrators Java no startup: `TabelasUsuarioNomenclaturaMigrator`, `EstatisticaClubeDataMigrator`, `CampeonatoParticipanteBackfill`.

## Documentação complementar

- [README raiz](../README.md)
- [Regras críticas](../docs/REGRAS_CRITICAS.md)
