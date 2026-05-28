# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository status

This is a **skeleton / scaffold** Spring Cloud microservices project. The Maven module structure, parent POM, and dependency versions are wired up, but no business code exists yet — every Java module currently contains only a single placeholder `org.example.Main` class with a `main` that prints "Hello and welcome!". The `docker/`, `sql/`, and `vue-vben-admin/` directories are empty placeholders for future infrastructure scripts, schema migrations, and the Vue frontend.

When adding features, assume you are bootstrapping (creating Spring Boot application classes, controllers, configuration, etc.) rather than modifying existing logic.

## Tech stack & versions (declared in root `pom.xml`)

- **Java 21** (`maven.compiler.source/target = 21`)
- **Spring Boot 3.5.0** (root POM's `<parent>` is `spring-boot-starter-parent:3.5.0`)
- **Spring Cloud 2025.0.0** — version property is declared but the BOM is **not yet imported** in `<dependencyManagement>`. Importing `spring-cloud-dependencies` will likely be needed before adding gateway/feign/cloud starters.
- **MyBatis-Plus 3.5.10.1** — version property declared but unused so far.
- **Spring AI 1.0.6** — only `demo-ai` depends on `spring-ai-model`.
- **Lombok 1.18.28**, **SnakeYAML 2.3**, **spring-boot-starter-aop**, **spring-boot-starter-actuator** are inherited by **every** module via the root `<dependencies>` block (not `<dependencyManagement>`), so submodules don't need to redeclare them.

## Module layout

Each submodule is an empty Maven child of the root `natural-demo` POM. Intended responsibilities (inferred from names — confirm with the user before locking in a design):

| Module | Intended role |
|--------|---------------|
| `demo-gateway` | Spring Cloud Gateway edge service |
| `demo-user` | User / auth service |
| `demo-business` | Core business service |
| `demo-supplier` | Supplier service |
| `demo-ai` | Spring AI integration (only module with `spring-ai-model`) |
| `demo-feign-client` | Shared Feign client interfaces consumed by other services |
| `demo-common` | Shared utilities, DTOs, base classes |

When creating application entry points, follow the existing package convention: `org.example` (the root group is `org.example`, version `1.0-SNAPSHOT`).

## Common commands

The project uses Maven (no `mvnw` wrapper checked in, despite the `.mvn/` folder existing). On PowerShell:

```powershell
# Build everything from the repo root
mvn clean install

# Build a single module (and its dependencies) without running the full reactor
mvn -pl demo-user -am clean install

# Skip tests
mvn clean install -DskipTests

# Run a single Spring Boot module (once a @SpringBootApplication exists)
mvn -pl demo-gateway spring-boot:run

# Run a single test class / method (once tests exist)
mvn -pl demo-user test -Dtest=SomeTest
mvn -pl demo-user test -Dtest=SomeTest#someMethod
```

`spring-boot-starter-test` is currently **commented out** in the root `pom.xml`. Uncomment it (or add it per-module) before writing tests.

## Things to watch for when extending the build

- The root `<properties>` has a typo: `paroject.build.sourceEncoding` (extra `a`). Submodule POMs use the correct `project.build.sourceEncoding`. Fix the root typo if you touch that block.
- Putting starters like `spring-boot-starter-web` / `webflux` in the root `<dependencies>` (currently commented out) would force **every** module to be a web app. Prefer adding web/webflux starters to specific modules, or move them under `<dependencyManagement>` and opt in per-module.
- `spring-cloud-dependencies` BOM needs to be added to `<dependencyManagement>` with `<type>pom</type><scope>import</scope>` before Spring Cloud starters (gateway, openfeign, loadbalancer, etc.) can be added without version conflicts.
