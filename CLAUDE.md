# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build y ejecución

```bash
./gradlew run          # ejecuta Main.kt
./gradlew build        # compila
./gradlew test         # lanza tests (JUnit 5)
./gradlew test --tests "io.github.scontreraslopez.trainsim.SomeTest"  # test individual
```

Kotlin 2.2.10, JVM toolchain 21, sin dependencias externas salvo `kotlin-test`.

---

## Arquitectura

El flujo de datos en cada paso de simulación es unidireccional:

```
Route.segmentAt(position) ──→ TrackSegment
                                    │
Driver.drive(train, segment) ──→ DriveCommand
                                    │
         PhysicsEngine.step(train, command, segment, dt)
                                    │
                               muta Train
                                    │
         SimulationObserver.onStep(train, command, segment, time)
```

**`Simulator`** orquesta el bucle. Para cuando `Scenario.isCompleted(train)` o se agota `SimulationConfig.maxTime`.

---

## Paquetes y responsabilidades

- **`model/`** — entidades del dominio: `Train` (estado mutable + parámetros físicos), `Station`, `Route`, `RouteEntry`, `TrackSegment`. Todo en SI internamente; los getters `*KmH()` y `*Km()` convierten para presentación.
- **`physics/`** — `PhysicsEngine` (object sin estado). El motor implementa Euler semi-implícito con ecuación de Davis, tracción por curva hiperbólica P/v y rampa en ‰.
- **`control/`** — `Driver` (interface), `DriveCommand` (throttle/brake ∈ [0,1]), `ManualDriver`, `AutopilotDriver` (pendiente).
- **`simulation/`** — `Simulator`, `SimulationConfig` (timeStep, maxTime).
- **`observer/`** — `SimulationObserver` (interface), `ConsoleLogger`, `CsvExporter` (pendiente), `ArrivalChecker` (pendiente).
- **`scenario/`** — `Scenario` (interface) y sus implementaciones concretas: `SimpleRouteScenario`, `HeavyLoadScenario` (pendiente), `ScenarioFactory` (pendiente).
- **`data/`** — `TrainRepository` (catálogo OpenTTD: Kirby Paul Tank, Chaney Jubilee, CS 4000, Centennial) y `RouteRepository` (Cercanías C-1 Murcia–Alicante, 11 estaciones), ambos `object`.

---

## Convenciones del proyecto

- **Unidades internas siempre en SI** (m, m/s, N, kg). Las conversiones solo en getters de presentación o en los repositorios al definir valores (`200.0 / 3.6`).
- **`grade` siempre en permil (‰)**, no en porcentaje. Anotado en todos los sitios donde aparece.
- `data class` para value objects (`TrackSegment`, `DriveCommand`, `SimulationConfig`). `class` para servicios con comportamiento (`Simulator`). `object` para singletons sin estado (`PhysicsEngine`, `TrainRepository`, `RouteRepository`).
- `Station` modela posiciones absolutas en la ruta (no coordenadas geográficas). `approachPoint < stopPoint < departurePoint` validado en `init`.

---

## TODOs conocidos

Ver sección **TODO / Mejoras pendientes** en `README.md`. Los más estructurales:

- Especialización de `Station` para terminales (sin `approachPoint` inicial / sin `departurePoint` final).
- `TrackSegment` dinámico con tramos de pendiente y restricciones de velocidad variables por posición.
- `AutopilotDriver` — pendiente de implementar.
- `CsvExporter` y `ArrivalChecker` — pendientes de implementar.
- `HeavyLoadScenario` y `ScenarioFactory` — pendientes de implementar.
- Control de velocidad de simulación (`realTimeFactor`) con corutinas.
- Terminal interactiva con input no bloqueante (toggle autopiloto/manual, W/S/B).
- Visualización ASCII de posición del tren en la ruta.
