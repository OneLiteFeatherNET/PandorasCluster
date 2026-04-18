# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

PandorasCluster is a Paper/Folia land-management plugin for Minecraft (Paper API `1.21.8-R0.1-SNAPSHOT`, declared `apiVersion` `1.20`). Plugin `main` class: `net.onelitefeather.pandorascluster.PandorasClusterPlugin`. Version is set in `gradle.properties` (`999.0.0` locally; releases override via `-Pversion=...`). Group: `net.onelitefeather`.

## Build, test, run

All commands use the wrapper from the repo root.

- Full build + tests (the CI task): `./gradlew clean build test`
- Build only: `./gradlew build`
- All tests: `./gradlew test`
- Tests for one module: `./gradlew :common:test`
- Single test class/method: `./gradlew :common:test --tests 'net.onelitefeather.pandorascluster.api.SomeTest.someMethod'`
- Shaded plugin jar: `./gradlew :plugin:shadowJar` (output in `plugin/build/libs/`)
- Publish to OneLiteFeather Maven: `./gradlew publish` (requires `ONELITEFEATHER_MAVEN_USERNAME` / `ONELITEFEATHER_MAVEN_PASSWORD`)
- Start a dev Paper server (downloads MC 1.21.4, EULA auto-accepted, `-Xmx4G`): `./gradlew :plugin:runServer`
- Local Postgres for manual testing: `docker compose -f docker/pandoras-cluster-db/docker-compose.yml up -d` (user/password/db all `pandoras_cluster`, port `5432`)

Toolchain: every module pins Gradle JVM toolchain `21`. GitHub Actions (`.github/workflows/build-pr.yml`) provisions Temurin **24** — local JDK ≥ 21 is sufficient; Gradle provisions 21 automatically if needed. Tests run on JUnit Jupiter 6 (`junit-bom`).

## Multi-module layout

`settings.gradle.kts` wires five Gradle modules plus a central `libs` version catalog (all dependency versions live there, not in module build files):

- `api` — pure interfaces + DTOs. No Paper dependency. Defines `PandorasCluster` facade and service interfaces (`LandService`, `LandPlayerService`, `LandAreaService`, `LandFlagService`, `DatabaseService`, `StaffNotificationService`) plus `dto/` records and `mapper/` contracts.
- `adapters/database` — Hibernate persistence adapter. Contains JPA entity `models/` (land/chunk/player/flag/position), `service/` implementations backed by Hibernate sessions, and `mapper/` implementations translating entities ↔ DTOs. `compileOnly` dependency on `:api`.
- `adapters/bukkit` — placeholder module (`.gitkeep` only today). Intended home for Bukkit-specific adapter code when it moves out of `:plugin`.
- `common` — wiring layer. `PandorasClusterImpl` builds the Hibernate `SessionFactory` (via `connection.cfg.xml` → `hibernate.cfg.xml`) and composes the database-backed service implementations. Includes `ThreadHelper` and the JUnit test suite (tests run against the H2 `connection.cfg.xml` in `common/src/test/resources`).
- `plugin` — the Paper entry point. Owns command handling (Incendo Cloud + Cloud Annotations), listeners, `paper-plugin.yml` generation (via `net.minecrell.plugin-yml.paper`), shaded jar packaging, and Paper-specific utilities. Depends on `:api` and `:common`.

Dependency direction is strict: `plugin → common → adapters/database → api`. Never add a reverse edge (e.g. `api` must not depend on Paper or Hibernate types it doesn't already import).

## Runtime bootstrap

`PandorasClusterPlugin.onEnable()` constructs `PandorasClusterImpl`, registers it with Bukkit's `ServicesManager` at `ServicePriority.Highest` so other plugins resolve `PandorasCluster.class`, then registers commands via `PaperCommandService` and the player-connection listener. `PandorasClusterImpl` uses `ThreadHelper.syncThreadForServiceLoader(...)` to build the Hibernate `SessionFactory` — the sync wrapper exists so `ServiceLoader` calls inside Hibernate see the plugin classloader. If the session factory fails to build, `databaseService` is left null and the remaining services are not initialized; downstream code must tolerate that.

Platform-specific helpers are resolved through `java.util.ServiceLoader`. Example: `META-INF/services/net.onelitefeather.pandorascluster.api.util.PlayerUtil` → `net.onelitefeather.pandorascluster.util.BukkitPlayerUtil`. When adding a new platform adapter (e.g. the pending `adapters/bukkit`), wire it via a `META-INF/services` file rather than hard-referencing it from `api` or `common`.

Hibernate config lookup is layered: `PandorasClusterImpl` calls `.configure().configure("connection.cfg.xml")`. That means the base `hibernate.cfg.xml` (shipped in `common` and `plugin` resources) is overlaid by `connection.cfg.xml` — keep connection details (URL/user/password/dialect) in the overlay so the base file stays portable across Postgres/MariaDB/H2.

## Commands, flags, permissions

Commands are registered with Incendo Cloud Annotations through `PaperCommandService`; Adventure platform is used for all player messaging. Permissions are **declared in `plugin/build.gradle.kts`** under the `paper { permissions { ... } }` block, grouped by default: OP-only admin perms, `TRUE` user perms, and `FALSE` flag toggle perms. Do not duplicate them in a hand-written `plugin.yml` — the `plugin-yml.paper` Gradle plugin generates the manifest at build time. Land flags are seeded from `plugin/src/main/resources/{entityCapFlags,naturalFlags,roleFlags}.json`; localizations live alongside as `pandorascluster_<locale>.properties`.

## Conventions worth knowing

- Commit messages follow Conventional Commits. Releases are cut by `semantic-release` (`.releaserc.json`) which runs `./gradlew check` then `./gradlew -Pversion=<next> publish` — keep the `check` task green.
- Renovate manages all dependency bumps; prefer updating versions in `settings.gradle.kts`'s `libs` catalog over pinning them inline in a module.
- Checkstyle config lives at `config/checkstyle/checkstyle.xml` (not currently wired into a `checkstyle` Gradle plugin — treat it as reference style).
- `rootProject.group` is interpolated into `paper.main`, so the package path `net.onelitefeather.pandorascluster.PandorasClusterPlugin` is load-bearing — renaming the main class or package requires updating `plugin/build.gradle.kts`.
