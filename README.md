## DocRegistry

Java/Spring Boot микросервис для работы с документами и PostgreSQL, построенный по слоистой архитектуре (api → application → domain → infrastructure).

### Запуск PostgreSQL

- **Запустить БД**:

```bash
docker compose up -d
```

Будет поднят PostgreSQL на `localhost:5432`, БД `docregistry`, пользователь/пароль `docregistry/docregistry`.

### Запуск приложения

- **Сборка и запуск**:

```bash
mvn spring-boot:run
```

Сервис поднимется на `http://localhost:8080`.

- **OpenAPI / Swagger UI**: `http://localhost:8080/swagger-ui.html`

### Основные возможности API

- **Создание документа**: `POST /api/documents`
- **Получение документа с историей**: `GET /api/documents/{id}`
- **Список документов (пагинация, сортировка)**: `GET /api/documents`
- **Поиск с фильтрами**: `POST /api/documents/search`
- **Пакетный submit (DRAFT -> SUBMITTED)**: `POST /api/documents/submit`
- **Пакетный approve (SUBMITTED -> APPROVED)**: `POST /api/documents/approve`
- **Проверка конкурентного утверждения**: `POST /api/documents/{id}/concurrency-approve-test`

Единый формат ошибок: JSON вида `{"code":"...","message":"..."}`.

### Фоновые процессы

- **SUBMIT-worker**: периодически берёт DRAFT-документы и отправляет на согласование.
- **APPROVE-worker**: берёт SUBMITTED-документы и утверждает.

Параметры настраиваются в `application.yml` (`workers.submit.*`, `workers.approve.*`).

### Утилита генерации документов

Утилита `DocumentGeneratorApp` создаёт N документов через HTTP API.

Запустить:

```bash
mvn -q -DskipTests package
java -cp target/doc-registry-0.0.1-SNAPSHOT.jar com.nikitamorozov.docregistry.infrastructure.tools.DocumentGeneratorApp
```

В логах будет прогресс вида `Progress: N/created`.

### Логи

- Создание документов, пакетные операции и фоновые worker-ы логируются в категорию `com.nikitamorozov.docregistry`.
- Уровень логирования и формат можно настроить через `application.yml` или стандартный `logback-spring.xml`.

### Архитектура слоёв

Проект организован по принципам Clean Architecture со следующими слоями:

- **API слой** (`com.nikitamorozov.docregistry.api`):
  - `controller/` — REST-контроллеры
  - `dto/` — DTO запросов/ответов (каждый класс в отдельном файле)
  - `error/` — глобальный обработчик ошибок и исключения API

- **Application слой** (`com.nikitamorozov.docregistry.application`):
  - `port.in/` — входные порты (use cases/команды/запросы)
  - `port.out/` — выходные порты (интерфейсы репозиториев и внешних сервисов)
  - `service/` — application-сервисы, реализующие use cases и оркестрирующие операции

- **Domain слой** (`com.nikitamorozov.docregistry.domain`):
  - `model/` — чистые доменные сущности без зависимостей от JPA/Spring
  - Enum-ы: `DocumentStatus`, `StatusAction`
  - Доменные модели содержат бизнес-логику переходов статусов

- **Infrastructure слой** (`com.nikitamorozov.docregistry.infrastructure`):
  - `persistence.jpa.entity/` — JPA-сущности (отображение таблиц БД)
  - `persistence.jpa.repository/` — Spring Data JPA репозитории
  - `persistence.jpa.adapter/` — адаптеры, реализующие выходные порты (`port.out`)
  - `persistence.jpa.mapper/` — MapStruct-мэпперы для конвертации домен ↔ JPA
  - `worker/` — фоновые воркеры (SUBMIT/APPROVE)
  - `tools/` — утилиты (например, генератор документов)

**Принципы зависимостей:**
- `api` → `application` (через `port.in`)
- `application` → `domain` + `port.out` (интерфейсы)
- `domain` — независим (только Java стандартная библиотека)
- `infrastructure` → `domain` + `application` (реализует `port.out`)

Для маппинга между доменом и JPA используется MapStruct (см. `infrastructure.persistence.jpa.mapper`).

