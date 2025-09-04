# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Prerequisites
- Java 17 or higher (currently using Java 11, but Spring Boot 3.4.6 requires Java 17+)
- MySQL database running on localhost:3307
- Gradle wrapper is included in the project

### Build and Test Commands
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run application
./gradlew bootRun

# Clean build
./gradlew clean build

# Run specific test
./gradlew test --tests "com.dong.bible.application.service.BookQueryServiceTest"

# Check dependencies
./gradlew dependencies
```

### Important Notes
- The project requires Java 17+ but current environment has Java 11
- MySQL connection configured for localhost:3307 with credentials in application-local.properties
- Application runs on Spring Boot 3.4.6 with profiles: local, dev, test

## Architecture Overview

This is a Spring Boot application following **Domain-Driven Design (DDD)** principles with Clean Architecture. The project is currently undergoing DDD refactoring (as indicated by the feature branch `feature/ddd-refactoring`).

### Layer Structure
```
src/main/java/com/dong/bible/
├── domain/           # Domain layer (entities, value objects, repositories)
├── application/      # Application layer (use cases, DTOs, services)
├── infrastructure/   # Infrastructure layer (JPA, mappers, external concerns)
├── web/             # Web layer (controllers, request/response DTOs)
└── common/          # Cross-cutting concerns (config, errors, utils)
```

### Key Domain Aggregates
1. **Book** (`domain/book/`) - Bible books with chapters and metadata
2. **Verse** (`domain/verse/`) - Bible verses with references and content
3. **DailyVerse** (`domain/dailyverse/`) - Daily devotional verses
4. **Sermon** (`domain/sermon/`) - Sermon content with media and references
5. **Category** (`domain/category/`) - Bible categorization

### Domain Objects Pattern
- **Entities**: Rich domain objects with business logic (e.g., `Book`, `BibleVerse`)
- **Value Objects**: Immutable objects like `BookName`, `VerseReference`, `VerseContent`
- **Repositories**: Domain interfaces implemented in infrastructure layer
- **Services**: Application services coordinate domain objects and return DTOs

### Data Access Pattern
- **JPA Repositories**: Infrastructure layer with `*JpaRepository` interfaces
- **Mappers**: Convert between JPA entities and domain objects using MapStruct
- **Repository Implementations**: Bridge domain repository interfaces to JPA

### Key Technologies
- Spring Boot 3.4.6
- Spring Data JPA + MyBatis
- MySQL 8
- MapStruct for mapping
- Lombok for boilerplate reduction
- JUnit 5 for testing

### Testing Structure
- Unit tests in `src/test/java/` mirror the main package structure
- Domain tests focus on business logic
- Application service tests verify use cases
- Repository tests verify data access

### Configuration
- **Profiles**: local, dev, test
- **Database**: MySQL with HikariCP connection pooling
- **JPA**: Show SQL enabled in local profile
- **Custom Properties**: Upload directories, transaction logging options

### Important Conventions
- Domain objects use static factory methods (`of()`, `from()`)
- Application services return DTOs, not domain objects
- Infrastructure mappers handle entity-domain conversions
- Web controllers use separate request/response DTOs
- All repository methods in domain layer return domain objects

### Legacy Code Notice
Some files still exist from pre-DDD structure and may be deprecated:
- Old service implementations in `service/impl/`
- Direct JPA usage in some controllers
- MapStruct mappers in root `mapstruct/` package (being migrated)

The codebase is actively being refactored to pure DDD principles, so prefer the newer domain-driven patterns when making changes.

## Sequential Thinking Guidelines
- **USE mcp__sequential-thinking__sequentialthinking tool for all complex problem-solving**
- Apply systematic thinking process for multi-step tasks and architectural decisions
- Break down complex requirements into structured thought processes
- Use sequential thinking before making significant code changes or design decisions

## Working Style
- **ALWAYS ask for permission before making any code changes**
- Analyze and explain the issue first, then propose a solution
- Wait for user approval before implementing changes
- Only make changes after explicit user consent