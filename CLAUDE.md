# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ApproveJ Workshop — a Spring Boot service (Kotlin) for learning approval testing with the [ApproveJ](https://approvej.org) library. The service provides APIs for virtual shopping carts and article management, with articles imported via Kafka.

## Build & Test Commands

```bash
./gradlew check              # Full build: compile, test, spotless formatting check
./gradlew test               # Run all tests only
./gradlew spotlessApply      # Auto-format all code (ktfmt Google style)
./gradlew spotlessCheck      # Verify formatting without changing files
./gradlew jacocoTestReport   # Generate code coverage report (XML)
```

Run a single test class:
```bash
./gradlew test --tests "org.approvej.workshop.service.application.article.ArticleManagerTest"
```

Run a single test method:
```bash
./gradlew test --tests "org.approvej.workshop.service.application.article.ArticleManagerTest.some test name"
```

**Requirements:** JDK 21, Docker (for TestContainers-based integration tests).

## Architecture

Hexagonal Architecture (ports & adapters) under `src/main/kotlin/org/approvej/workshop/service/`:

- **`application/`** — Domain logic and port interfaces
  - `article/` — `Article` value object, `ArticleManager`, ports `ToStoreArticles` and `ToManagerArticles`
  - `shoppingcart/` — `ShoppingCart`/`Item` value objects, `ShoppingCartManager`, ports `ToStoreShoppingCarts` and `ToManageShoppingCarts`

- **`adapters/demanding/`** — Inbound adapters (receive requests)
  - `http/article/` — `ArticleController` (GET `/articles?q=`)
  - `http/shoppingcart/` — `ShoppingCartController` (GET/POST `/shopping-cart`)
  - `kafka/article/` — `ArticleConsumer` (consumes from `article` topic)

- **`adapters/providing/`** — Outbound adapters (persistence via JPA)
  - `database/article/` — `ArticleStore` implements `ToStoreArticles`
  - `database/shoppingcart/` — `ShoppingCartStore` implements `ToStoreShoppingCarts`

Port interfaces live in `application/`, adapters implement them. Domain objects use `BigDecimal` for prices; DTOs convert to cents (`Int`) for the HTTP API.

## Testing Patterns

Tests mirror the source structure under `src/test/kotlin/`.

- **Application (unit) tests** — Use hand-written stubs (`ArticleStoreStub`, `ShoppingCartStoreStub`) instead of mocks or Spring context. Test builders: `anArticle()`, `aShoppingCart()`, `anItem()` with randomized defaults via stubit-random.

- **Adapter (integration) tests** — `@SpringBootTest` with TestContainers (PostgreSQL, Kafka). HTTP tests use Java's native `HttpClient`. `TestcontainersConfiguration` provides container beans with `@ServiceConnection`.

- **Approval testing** — ApproveJ with `approve(value).byFile()`. Approved files (`.json`/`.txt`) live next to test files. Config in `src/test/resources/approvej.properties`. Inventory tracked in `.approvej/inventory.properties`.

## Code Formatting

Spotless enforces ktfmt Google style. Formatting failures break `./gradlew check`. Run `./gradlew spotlessApply` before committing. Kotlin uses 2-space indentation, max line length 100.

## Tech Stack

- Kotlin 2.3.0, Java 21
- Spring Boot 3.5.7 (Web, Data JPA, Kafka)
- Jackson 2.20.1 (with Kotlin module)
- PostgreSQL, TestContainers 1.21.4
- JUnit Jupiter 6.0.2, AssertJ 3.27.6, Awaitility
- ApproveJ 1.3-SNAPSHOT (core + json-jackson)
