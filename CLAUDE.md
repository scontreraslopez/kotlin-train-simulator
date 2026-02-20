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
Scenario.environment
    └─ conditionsAt(position) ──→ TrackConditions
                                        │
Driver.drive(train, conditions) ──→ DriveCommand
                                        │
              PhysicsEngine.step(train, command, conditions, dt)
                                        │
                                  muta Train
                                        │
              SimulationObserver.onStep(train, command, conditions, time)
```

**`Simulator`** orquesta el bucle. Para cuando `Scenario.isCompleted(train)` o se agota `SimulationConfig.maxTime`.

---

## Paquetes y responsabilidades

- **`model/`** — entidades del dominio: `Train` (estado mutable + parámetros físicos), `Station`, `Route`, `RouteEntry`. Todo en SI internamente; los getters `*KmH()` y `*Km()` convierten para presentación.
- **`physics/`** — `PhysicsEngine` (object sin estado), `TrackConditions`, `Environment` (interface), `StaticEnvironment`. El motor implementa Euler semi-implícito con ecuación de Davis, tracción por curva hiperbólica P/v y rampa en ‰.
- **`control/`** — `Driver` (interface), `DriveCommand` (throttle/brake ∈ [0,1]), `ManualDriver`, `AutopilotDriver` (pendiente).
- **`simulation/`** — `Simulator`, `SimulationConfig` (timeStep, maxTime), `Scenario` (interface).
- **`observer/`** — `SimulationObserver` (interface), `ConsoleLogger`, `CsvExporter` (pendiente), `ArrivalChecker` (pendiente).
- **`scenario/`** — implementaciones concretas de `Scenario`: `SimpleRouteScenario`, `HeavyLoadScenario`.
- **`data/`** — `TrainRepository`: catálogo de trenes OpenTTD (Kirby Paul Tank, Chaney Jubilee, CS 4000, Centennial) con parámetros físicos estimados.

---

## Convenciones del proyecto

- **Unidades internas siempre en SI** (m, m/s, N, kg). Las conversiones solo en getters de presentación o en `TrainRepository` al definir valores (`200.0 / 3.6`).
- **`grade` siempre en permil (‰)**, no en porcentaje. Anotado en todos los sitios donde aparece.
- `data class` para value objects (`TrackConditions`, `DriveCommand`, `SimulationConfig`). `class` para servicios con comportamiento (`StaticEnvironment`, `Simulator`). `object` para singletons sin estado (`PhysicsEngine`, `TrainRepository`).
- `Station` modela posiciones absolutas en la ruta (no coordenadas geográficas). `approachPoint < stopPoint < departurePoint` validado en `init`.

---

## TODOs conocidos

Ver sección **TODO / Mejoras pendientes** en `README.md`. Los más estructurales:

- `RouteRepository` análogo a `TrainRepository`.
- Especialización de `Station` para terminales (sin `approachPoint` inicial / sin `departurePoint` final).
- `DynamicEnvironment` con tramos de pendiente y restricciones de velocidad variables.
- `AutopilotDriver` — pendiente de implementar.
- `CsvExporter` y `ArrivalChecker` — pendientes de implementar.
- Control de velocidad de simulación (`realTimeFactor`) con corutinas.
- Terminal interactiva con input no bloqueante (toggle autopiloto/manual, W/S/B).
- Visualización ASCII de posición del tren en la ruta.
