---
name: Postman and SpringBoot Docs
overview: "Create three documentation suites: (1) Postman collection + environment for all 68+ API endpoints organized by module, (2) API reference docs in docs/api/, and (3) a multi-file Spring Boot deep-dive in docs/springboot-docs/ covering how the system boots, binds, secures, and operates at staff-engineer depth."
todos:
  - id: postman-env
    content: Create docs/postman/Work-Platform.postman_environment.json with all variables
    status: completed
  - id: postman-collection
    content: Create docs/postman/Work-Platform.postman_collection.json with 14 folders, 68+ requests, auth scripts, variable chaining
    status: completed
  - id: api-reference
    content: Create docs/api/api-reference.md with all endpoints, request/response schemas, status codes
    status: completed
  - id: sb-01-boot
    content: Create docs/springboot-docs/01-how-the-app-boots.md
    status: completed
  - id: sb-02-autoconfig
    content: Create docs/springboot-docs/02-auto-configuration-vs-custom.md
    status: completed
  - id: sb-03-gradle
    content: Create docs/springboot-docs/03-multi-module-gradle-binding.md
    status: completed
  - id: sb-04-security
    content: Create docs/springboot-docs/04-security-architecture.md
    status: completed
  - id: sb-05-di
    content: Create docs/springboot-docs/05-dependency-injection-deep-dive.md
    status: completed
  - id: sb-06-jpa
    content: Create docs/springboot-docs/06-jpa-and-persistence-layer.md
    status: completed
  - id: sb-07-kafka
    content: Create docs/springboot-docs/07-kafka-and-event-driven.md
    status: completed
  - id: sb-08-redis
    content: Create docs/springboot-docs/08-redis-patterns.md
    status: completed
  - id: sb-09-websocket
    content: Create docs/springboot-docs/09-websocket-and-stomp.md
    status: completed
  - id: sb-10-testing
    content: Create docs/springboot-docs/10-testing-strategy.md
    status: completed
  - id: sb-11-config
    content: Create docs/springboot-docs/11-configuration-properties.md
    status: completed
  - id: sb-12-why-this-structure
    content: Create docs/springboot-docs/12-why-this-structure.md — compare todo vs work-platform, list all proven Spring Boot project structures, explain what Spring Boot features enable each approach
    status: completed
  - id: sb-13-error-handling
    content: Create docs/springboot-docs/13-error-handling-strategy.md
    status: completed
  - id: sb-14-concurrency
    content: Create docs/springboot-docs/14-concurrency-and-thread-model.md
    status: completed
  - id: sb-15-migrations
    content: Create docs/springboot-docs/15-database-migration-strategy.md
    status: completed
  - id: sb-16-api-design
    content: Create docs/springboot-docs/16-api-design-principles.md
    status: completed
  - id: sb-17-scalability
    content: Create docs/springboot-docs/17-scalability-and-evolution-path.md
    status: completed
  - id: sb-18-tradeoffs
    content: Create docs/springboot-docs/18-technology-tradeoffs.md
    status: completed
  - id: sb-19-staff-engineer-checklist
    content: Create docs/springboot-docs/19-staff-engineer-readiness-checklist.md
    status: completed
isProject: false
---

# Postman Collection + Spring Boot Learning Documentation

## 1. docs/postman/ — Postman Collection and Environment

Create two JSON files:

### [docs/postman/Work-Platform.postman_environment.json](docs/postman/Work-Platform.postman_environment.json)

Environment variables for local development:

- `baseUrl` = `http://localhost:8080`
- `accessToken` = (empty, set by login/register script)
- `refreshToken` = (empty, set by login/register script)
- `workspaceId`, `projectId`, `taskId`, `blockId`, `commentId`, `attachmentId` = (empty, set by test scripts)
- `userId` = (empty, set by /me response)

### [docs/postman/Work-Platform.postman_collection.json](docs/postman/Work-Platform.postman_collection.json)

Organized into **folders mirroring the modular architecture** (14 folders, one per controller):

- **Auth** (5 requests): Register, Login, Refresh, Logout, Me
  - Register/Login have **post-response scripts** that auto-set `accessToken` and `refreshToken` variables
- **Users** (4): Get Me, Get By ID, Update Profile, Search Users
- **Workspaces** (5): Create, Get, My Workspaces, Add Member, Remove Member
- **Projects** (6): Create, Get, List, Update, Delete, Add Member
  - Uses `{{workspaceId}}` variable
- **Tasks** (20): Full CRUD + tree, members, labels, join-requests (create + decide), subtask-proposals (create + decide), move, complete, restore, delete
  - Uses `{{projectId}}`, `{{taskId}}` variables
- **Content Blocks** (4): Append, List, Update, Delete
  - Uses `{{taskId}}`, `{{blockId}}`
- **Comments** (5): Add, List for Block, List for Task, Edit, Delete
  - Uses `{{taskId}}`, `{{blockId}}`, `{{commentId}}`
- **Attachments** (4): Upload (multipart), Download, List, Delete
- **Notifications** (4): List, Unread Count, Mark Read, Mark All Read
- **Search** (4): Tasks, Projects, Users, Autocomplete
- **Audit** (2): Activity for Entity, Activity for User
- **Analytics** (2): Project Dashboard, Workspace Dashboard
- **Presence** (2): Online Users, Heartbeat

Every request:

- Uses `{{baseUrl}}` for the host
- Includes `Authorization: Bearer {{accessToken}}` header (except register/login)
- Has appropriate Content-Type headers
- Has example request bodies with realistic data

## 2. docs/api/ — API Reference

### [docs/api/api-reference.md](docs/api/api-reference.md)

A single comprehensive markdown file listing all endpoints grouped by module with:

- HTTP method + full path
- Authentication requirement
- Request body fields with types and validation
- Response body fields with types
- Status codes
- WebSocket topics (at the end)

## 3. docs/springboot-docs/ — Spring Boot Staff-Level Deep Dive

**11 files**, each focused on one concept that a staff engineer must master to build and reason about this system:

### [docs/springboot-docs/01-how-the-app-boots.md](docs/springboot-docs/01-how-the-app-boots.md)

How Spring Boot starts from `main()` to serving requests:

- `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`
- `SpringApplication.run()` lifecycle: `ApplicationContext` creation, `BeanDefinitionRegistryPostProcessor`, auto-config, bean instantiation
- Why `scanBasePackages = "com.example.workplatform"` is needed (main class is in `apiapp` subpackage, modules are siblings)
- `@EntityScan` and `@EnableJpaRepositories` — why api-app declares them explicitly
- How the 31 Gradle JARs become one classpath at runtime

### [docs/springboot-docs/02-auto-configuration-vs-custom.md](docs/springboot-docs/02-auto-configuration-vs-custom.md)

What Boot gives for free vs what this project overrides:

- Auto-config: DataSource, JPA, Flyway, Kafka, Redis, Elasticsearch, Mail, Servlet, Jackson baseline
- Custom overrides: `SecurityFilterChain`, `ObjectMapper` (`@Primary`), `RedisTemplate` serializers, Kafka consumer factory with `JsonNode` + DLT, MinioClient conditional bean, STOMP broker
- Empty `@Configuration` classes — why they exist (documentation, future hook, package scanning anchor)
- `@ConditionalOnProperty` for storage mode switching
- The `@Primary` pattern for adapter selection

### [docs/springboot-docs/03-multi-module-gradle-binding.md](docs/springboot-docs/03-multi-module-gradle-binding.md)

How 31 Gradle subprojects become one running application:

- `settings.gradle` declares the module graph
- Each module is a `java-library` producing a JAR
- `apps/api-app` depends on all modules → Gradle builds a fat classpath
- Spring Boot plugin (`bootJar`) packages everything into an executable JAR
- `@ComponentScan("com.example.workplatform")` discovers `@Component`, `@Service`, `@Repository`, `@Configuration` across all JARs
- `@EntityScan` discovers `@Entity` classes across JARs
- `@EnableJpaRepositories` discovers `JpaRepository` interfaces across JARs
- Why this is NOT microservices — single process, single classpath, single database, in-process method calls

### [docs/springboot-docs/04-security-architecture.md](docs/springboot-docs/04-security-architecture.md)

How security works end-to-end:

- Spring Security filter chain: `DelegatingFilterProxy` → `FilterChainProxy` → our `SecurityFilterChain`
- Stateless session (`STATELESS` session creation policy) — no HTTP session, no cookies
- JWT flow: Register → issue tokens, Login → validate credentials + issue tokens, Request → `JwtAuthenticationFilter` extracts Bearer token → JJWT parses → `AuthenticatedUser` set in `SecurityContext`
- Public vs protected paths (`SecurityConstants.PUBLIC_PATHS`)
- `@CurrentUser` resolver: `HandlerMethodArgumentResolver` extracts `AuthenticatedUser` from `SecurityContext`
- `@EnableMethodSecurity` and `@PreAuthorize` (available but most authz is in use-case policies)
- Why business authorization (policies) lives in domain/application, not in Spring Security voters

### [docs/springboot-docs/05-dependency-injection-deep-dive.md](docs/springboot-docs/05-dependency-injection-deep-dive.md)

How DI wires everything together across modules:

- Constructor injection (no `@Autowired` on fields)
- Port/Adapter wiring: `TaskRepository` (interface in `application/port/`) → `JpaTaskRepositoryAdapter` (`@Component` in `infrastructure/persistence/`) → Spring finds the implementation and injects it
- `@Primary` for competing beans (e.g., `RedisPubSubBroadcastAdapter` vs `StompBroadcastAdapter`)
- `List<NotificationChannelPort>` injection — Spring collects all implementations
- `Map<StorageMode, ObjectStoragePort>` — manual map construction in `@Configuration`
- `@ConditionalOnProperty` — beans that only exist under certain config
- Cross-module wiring: `modules/tasks` depends on `shared/kernel` → Spring finds `UuidGeneratorAdapter` from kernel and injects it into task use cases

### [docs/springboot-docs/06-jpa-and-persistence-layer.md](docs/springboot-docs/06-jpa-and-persistence-layer.md)

How the persistence layer works:

- `spring-boot-starter-data-jpa` auto-configures `EntityManagerFactory`, `TransactionManager`, connection pool (HikariCP)
- `ddl-auto: validate` — Hibernate validates schema against entities but does NOT modify it (Flyway owns schema)
- `open-in-view: false` — disables OSIV anti-pattern (no lazy loading in controllers)
- JPA entity lifecycle: `@PrePersist`, `@PreUpdate` for timestamps
- Spring Data: `JpaRepository<T, ID>` generates SQL at startup from method names
- Custom queries: `@Query` with JPQL or native SQL
- Why domain models ≠ JPA entities (framework isolation, testability)
- The mapper pattern: `TaskJpaMapper.toDomain()` / `toEntity()`
- Transaction boundaries: `@Transactional` on use cases, outbox write in same TX

### [docs/springboot-docs/07-kafka-and-event-driven.md](docs/springboot-docs/07-kafka-and-event-driven.md)

How Kafka integrates:

- `spring-kafka` auto-config: `KafkaTemplate`, `ConsumerFactory`, `ProducerFactory`
- `@EnableKafka` enables `@KafkaListener` annotation processing
- Custom `ConsumerFactory<String, JsonNode>` with `ErrorHandlingDeserializer` → graceful deserialization failures
- Dead letter topic routing: failed messages go to `<topic>.dlt`
- `DomainEventKafkaListener` + `EventHandlerRegistry` — generic dispatch by `eventType` header
- Outbox relay: `@Scheduled` reads `outbox_events` → publishes to Kafka → marks published
- Idempotent consumers: `projection_events` table prevents reprocessing
- Why KRaft (no ZooKeeper): simpler setup, Kafka 3.7+ default

### [docs/springboot-docs/08-redis-patterns.md](docs/springboot-docs/08-redis-patterns.md)

How Redis is used for multiple purposes:

- **Caching**: `RedisTemplate` with JSON serialization
- **Distributed locks**: `setIfAbsent` with TTL for exclusive operations
- **Pub/Sub for real-time**: `RedisMessageListenerContainer` + `RedisPubSubBroadcastAdapter` for cross-node WebSocket fanout
- **Presence**: Sorted sets (`ZADD`, `ZRANGEBYSCORE`, `ZREM`) with score = timestamp for TTL-like behavior
- **Rate limiting**: Placeholder for token bucket implementation
- Why Redis vs alternatives: single technology for cache + locks + presence + pub/sub reduces operational complexity

### [docs/springboot-docs/09-websocket-and-stomp.md](docs/springboot-docs/09-websocket-and-stomp.md)

How real-time works:

- `@EnableWebSocketMessageBroker` sets up Spring's STOMP support
- SockJS fallback at `/ws` endpoint
- Broker prefixes `/topic` (broadcast) and `/queue` (point-to-point)
- Application prefix `/app` for client-to-server messages
- `SimpMessagingTemplate.convertAndSend()` pushes to subscribers
- Multi-node problem: simple STOMP broker is in-memory per node → Redis Pub/Sub bridges nodes
- Session events: `SessionConnectedEvent`, `SessionDisconnectEvent` for presence tracking
- JWT authentication on WebSocket: token sent as query parameter on CONNECT

### [docs/springboot-docs/10-testing-strategy.md](docs/springboot-docs/10-testing-strategy.md)

How to test this architecture:

- **Unit tests**: Domain models and policies — plain JUnit, no Spring, no DB. Use `TestClock` and `TestIdGenerator`.
- **Integration tests**: Use `IntegrationTestContainers` (Postgres, Kafka, Redis, Elasticsearch) with `@DynamicPropertySource`
- **Slice tests**: `@WebMvcTest` for controllers, `@DataJpaTest` for repositories
- **Architecture tests**: ArchUnit to enforce hexagonal rules (domain cannot import infrastructure)
- **Contract tests**: Verify port implementations match interface contracts
- `shared/testing` module: reusable test infrastructure shared across all modules

### [docs/springboot-docs/11-configuration-properties.md](docs/springboot-docs/11-configuration-properties.md)

How configuration flows from YAML to beans:

- `application.yml` → Spring Environment → `@Value` or `@ConfigurationProperties`
- Type-safe config: `JwtProperties`, `StorageProperties` bind YAML subtrees to records/POJOs
- `@EnableConfigurationProperties` registers them
- Environment variable overrides: `${POSTGRES_HOST:localhost}` pattern
- Profile-based config: `application-{profile}.yml` (not used yet, but ready)
- Worker vs API split: same YAML keys, different values (e.g., `web-application-type: none`)
- Feature flags: `workplatform.outbox.relay.enabled`, `workplatform.notifications.email.enabled`

### [docs/springboot-docs/12-why-this-structure.md](docs/springboot-docs/12-why-this-structure.md)

**The most important document.** Why we chose this structure, how it compares to alternatives, and what Spring Boot properties enable the flexibility.

**Part 1 — The todo app vs work-platform (direct comparison):**

- `todo/` = single-module Gradle, single `build.gradle`, all code under one `src/main/java`
- `work-platform/` = 31 Gradle subprojects, each with own `build.gradle` and `src/main/java`
- Same framework (Spring Boot 4), same language (Java 21), radically different organization
- What changed: team ambition, module count, infrastructure count, need for compile-time boundary enforcement

**Part 2 — All proven ways to structure a Spring Boot project (5 approaches):**

1. **Single module, package-by-layer** (`controller/`, `service/`, `repository/`, `model/`)

- Simplest, good for <10 entity types
- Problem: any class can import any other class, no enforcement

1. **Single module, package-by-feature** (`user/`, `task/`, `project/`)

- Better cohesion, each feature is self-contained
- Problem: still no compile-time boundary — accidental cross-imports possible

1. **Single module, hexagonal per feature** (what `todo/` evolved toward)

- Each feature has `api/`, `application/`, `domain/`, `infrastructure/`
- Good separation but relies on developer discipline (or ArchUnit)

1. **Multi-module Gradle, hexagonal per module** (what `work-platform/` uses)

- Compile-time boundaries, explicit dependency graph, selective classpaths
- Cost: more build files, longer initial setup, mapper boilerplate

1. **Microservices** (each module is a separate deployable + database)

- Maximum isolation, independent scaling, independent deployment
- Cost: distributed transactions, network latency, operational complexity

For each approach: when to use it, when to outgrow it, real-world examples, and the migration path from one to the next.

**Part 3 — What Spring Boot features enable this flexibility:**

- `@SpringBootApplication` = `@ComponentScan` + `@EnableAutoConfiguration` — the scan scope determines what's discovered
- `scanBasePackages` — can be widened or narrowed to control what enters the context
- `@EntityScan` and `@EnableJpaRepositories` — control which packages are scanned for JPA
- Multi-JAR classpath — Spring doesn't care if classes come from one JAR or 31 JARs; it scans the combined classpath
- `@Configuration` in any JAR on the classpath is discovered
- `spring-boot-starter-` starters are just dependency bundles — they work identically in single or multi-module
- `@ConditionalOnClass`, `@ConditionalOnProperty` — auto-config adapts to what's on the classpath
- Gradle `java-library` vs `application` plugin — controls whether a module is a library or an executable
- Spring Boot `bootJar` task packages all dependency JARs into one fat JAR

**Part 4 — The thinking process that led to our choice:**

- Started with todo app (approach 3)
- Realized 15+ modules + 7 infrastructure modules = too many boundaries to trust developer discipline alone
- Needed Redis, Kafka, Elasticsearch, WebSocket, JobRunr — different modules need different subsets
- Wanted two deployable apps (API + Worker) sharing the same modules
- Multi-module Gradle (approach 4) was the natural next step
- Didn't go to microservices (approach 5) because: single developer, single database is fine, no need for independent deployment

### [docs/springboot-docs/13-error-handling-strategy.md](docs/springboot-docs/13-error-handling-strategy.md)

How errors flow from domain to HTTP response:

- Exception hierarchy: `DomainException` → `BusinessRuleViolationException`, `*NotFoundException`, `InvalidCredentialsException`
- `ApiExceptionHandler` (`@RestControllerAdvice`) catches and maps:
  - `DomainException` → 400 BAD_REQUEST (with message)
  - `*NotFoundException` → 404 NOT_FOUND
  - `AccessDeniedException` (IAM) → 403 FORBIDDEN
  - `MethodArgumentNotValidException` → 400 with field-level errors
  - Uncaught exceptions → 500 with correlation ID
- `ApiErrorResponse` record: consistent JSON structure (`timestamp`, `status`, `error`, `message`, `correlationId`)
- Why domain exceptions don't know about HTTP status codes (layer separation)
- Error logging strategy: 4xx = WARN, 5xx = ERROR with stack trace
- Kafka consumer error handling: `DeadLetterPublishingRecoverer` → DLT topic

### [docs/springboot-docs/14-concurrency-and-thread-model.md](docs/springboot-docs/14-concurrency-and-thread-model.md)

How threads work in this system:

- **Tomcat thread pool** (api-app): default 200 threads for HTTP requests; each request gets one thread
- **Java 21 virtual threads**: available via `spring.threads.virtual.enabled=true` (not yet enabled but ready)
- **WebSocket threads**: separate thread pool for STOMP message handling
- **Kafka consumer threads**: one thread per partition per consumer group; `ConcurrentKafkaListenerContainerFactory` controls concurrency
- **@Scheduled threads**: Spring's `TaskScheduler` thread pool (default 1 thread — multiple `@EnableScheduling` don't multiply this)
- **JobRunr threads**: separate background job processing threads managed by JobRunr
- **Redis Pub/Sub listener**: `RedisMessageListenerContainer` runs on its own thread pool
- **Database connection pool**: HikariCP (default 10 connections) — shared across all threads
- **Optimistic locking**: `version` column on tasks prevents lost updates under concurrent writes
- **Distributed locks**: `DistributedLockService` for cross-instance mutual exclusion (outbox relay, job registration)
- Common pitfalls: blocking a Kafka consumer thread, exhausting the connection pool, `@Transactional` scope too wide

### [docs/springboot-docs/15-database-migration-strategy.md](docs/springboot-docs/15-database-migration-strategy.md)

How schema evolves safely:

- Flyway runs on app startup (`spring.flyway.enabled=true`)
- Migrations are immutable — once applied, never modified
- Naming convention: `V{number}__{description}.sql`
- Forward-only: no `DOWN` migrations (rollback by writing a new `UP` migration)
- Zero-downtime migration patterns: add column (nullable), backfill, make NOT NULL, then drop old column
- V016 example: `DROP TABLE IF EXISTS attachments; CREATE TABLE attachments ...` — acceptable in dev, dangerous in prod (data loss). Prod approach: add new columns, migrate data, drop old columns.
- How Flyway and JPA validate work together: Flyway modifies schema, then `ddl-auto: validate` ensures entities match
- Multi-app consideration: only one app should run migrations (api-app does, worker-app could be configured to skip)

### [docs/springboot-docs/16-api-design-principles.md](docs/springboot-docs/16-api-design-principles.md)

REST API conventions used in this project:

- Resource-oriented URLs: `/api/workspaces/{id}/projects` (nested where ownership is clear)
- HTTP methods: POST (create), GET (read), PATCH (partial update), DELETE (remove)
- Status codes: 201 for creation, 204 for deletion, 400 for validation, 404 for not found, 403 for forbidden
- Pagination: `page` + `size` query params, response wraps in `PageResponse`
- Filtering: query params (`?q=`, `?projectId=`, `?entityType=`)
- Authentication: Bearer JWT in `Authorization` header
- Error format: consistent `ApiErrorResponse` JSON
- No HATEOAS (pragmatic choice — adds complexity without clear value for SPA clients)
- Idempotency: PUT would be idempotent, PATCH is not — but since we use UUIDs, re-POST is safe with conflict detection
- Versioning strategy: not implemented yet, but path-based (`/api/v2/`) is the recommended approach

### [docs/springboot-docs/17-scalability-and-evolution-path.md](docs/springboot-docs/17-scalability-and-evolution-path.md)

What breaks first at scale and how to evolve:

- **Bottleneck 1: Single Postgres** — read replicas, connection pooling (PgBouncer), table partitioning for outbox/audit
- **Bottleneck 2: Elasticsearch indexing lag** — async indexing via Kafka is already in place; tune bulk size and refresh interval
- **Bottleneck 3: WebSocket connections** — Redis Pub/Sub is already the cross-node layer; add sticky sessions or external STOMP broker (RabbitMQ)
- **Bottleneck 4: Kafka consumer throughput** — increase partitions, add consumer instances (worker-app horizontal scaling)
- **Evolution to microservices**: which modules extract first (search, notifications, chat), what changes (module becomes service, port becomes HTTP/gRPC call, shared DB splits)
- **CQRS**: separate read models (Elasticsearch for search, materialized views for dashboards) from write models (Postgres) — partially done already
- **Event sourcing**: not needed now, but the outbox pattern is a stepping stone

### [docs/springboot-docs/18-technology-tradeoffs.md](docs/springboot-docs/18-technology-tradeoffs.md)

What we chose, what we didn't, and why:

- **Kafka vs RabbitMQ**: Kafka for durable log replay and ordering; RabbitMQ better for traditional work queues. We need replay (outbox) and ordering (per-entity events).
- **Postgres vs MongoDB**: Postgres for strong consistency, JSONB for flexible content, relational integrity. MongoDB would simplify block storage but lose transactional guarantees with tasks.
- **Redis vs Memcached**: Redis for data structures (sorted sets, pub/sub, locks). Memcached is simpler but can't do presence or pub/sub.
- **Elasticsearch vs Postgres full-text**: Elasticsearch for autocomplete (edge-ngram), relevance scoring, and decoupled read model. Postgres `tsvector` works for simple search but lacks autocomplete UX.
- **JobRunr vs Spring Batch vs Quartz**: JobRunr for simple recurring jobs with dashboard. Spring Batch for ETL (overkill here). Quartz for complex scheduling (overkill here).
- **STOMP/SockJS vs raw WebSocket vs SSE**: STOMP gives pub/sub semantics, topic subscriptions, and SockJS fallback. Raw WebSocket requires custom protocol. SSE is one-directional (no heartbeat from client).
- **Flyway vs Liquibase**: Flyway for SQL-first migrations (simple, predictable). Liquibase for XML/YAML migrations with rollback support (more complex).
- **MinIO vs local filesystem**: MinIO for S3-compatible API that mirrors production. Local FS for zero-dependency development. Strategy pattern makes it switchable.

### [docs/springboot-docs/19-staff-engineer-readiness-checklist.md](docs/springboot-docs/19-staff-engineer-readiness-checklist.md)

A self-assessment checklist organized by domain. For each item: what to know, where it appears in this project, and resources to go deeper.

**Architecture and Design:**

- Can you explain why you chose modular monolith over microservices and defend it?
- Can you draw the dependency graph between modules from memory?
- Can you identify which module to modify for a new feature without looking at code?
- Do you understand hexagonal architecture well enough to teach it?

**Spring Boot Internals:**

- Can you explain the full boot sequence from `main()` to first request?
- Can you predict which beans will be created from a given set of starters + config?
- Can you debug auto-configuration failures (`--debug` flag, conditions report)?
- Do you know when to use `@Configuration` vs `@AutoConfiguration`?

**Data and Persistence:**

- Can you write a zero-downtime migration for a column rename?
- Can you explain optimistic vs pessimistic locking and when to use each?
- Can you design an outbox table and explain why it's needed?
- Do you understand N+1 queries and how to prevent them?

**Distributed Systems:**

- Can you explain at-least-once vs exactly-once delivery?
- Can you design an idempotent consumer?
- Can you explain the CAP theorem and where this system sits?
- Can you identify single points of failure in this architecture?

**Security:**

- Can you explain the full JWT lifecycle (issue, validate, refresh, revoke)?
- Can you identify OWASP Top 10 risks and how they're mitigated here?
- Can you explain why CSRF is disabled (stateless API) and when it shouldn't be?

**Observability:**

- Can you correlate a request across API → Kafka → Worker using correlation IDs?
- Can you set up alerting thresholds for latency, error rate, and queue depth?
- Can you debug a production issue using only logs and metrics (no debugger)?

**Leadership and Communication:**

- Can you write an RFC/design doc for a new feature and get buy-in?
- Can you explain this architecture to a junior engineer in 10 minutes?
- Can you identify when the architecture is over-engineered and simplify it?
