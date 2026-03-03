# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build y ejecución

```bash
./gradlew run          # ejecuta Main.kt
./gradlew build        # compila
./gradlew test         # lanza tests (JUnit 5)
./gradlew test --tests "io.github.scontreraslopez.trainsim.SomeTest"  # test individual
```

Kotlin 2.2.10, JVM toolchain 21. Dependencias de test: `junit-jupiter:5.14.3` + `junit-platform-launcher:1.14.3`. Sin dependencias externas más allá de stdlib + JUnit.

---

## Arquitectura

El flujo de datos en cada paso de simulación es unidireccional:

```
Route.drivingContextAt(position) ──→ DrivingContext
                                           │  (segment + distanceToNextStop
                                           │   + zonas approach/departure/stop)
Driver.drive(train, context) ──────→ DriveCommand
                                           │
    PhysicsEngine.step(train, command, context.segment, dt)
                                           │
                                      muta Train
                                           │
    SimulationObserver.onStep(train, command, context, time)
```

**`Simulator`** orquesta el bucle. Para cuando `Scenario.isCompleted(train)`, `drivingContextAt` devuelve `null` (estación terminal alcanzada), o se agota `SimulationConfig.maxTime` (default: 86400 s). **`SimulationFactory`** realiza el wiring de dependencias antes de crear el `Simulator`. **`Main.kt`** es el Composition Root.

---

## Paquetes y responsabilidades

- **`model/`** — `Train` (estado mutable: position + velocity; parámetros físicos inmutables), `Station`, `Route`, `RouteEntry`, `TrackSegment`, `DrivingContext`. Todo en SI internamente; los getters `*KmH()` y `*Km()` convierten para presentación.
- **`physics/`** — `PhysicsEngine` (object sin estado). Euler semi-implícito con ecuación de Davis, tracción por curva hiperbólica P/v y rampa en ‰. Actualiza velocity primero, luego position.
- **`control/`** — `Driver` (interface: `drive(train, context): DriveCommand`), `DriveCommand` (throttle/brake ∈ [0,1]), `ManualDriver`, `AutopilotDriver` (máquina de 5 estados — ver abajo).
- **`simulation/`** — `Simulator`, `SimulationConfig` (timeStep, maxTime), `SimulationFactory`.
- **`observer/`** — `SimulationObserver` (interface), `ConsoleLogger` (implementado), `CsvExporter` y `ArrivalChecker` (stubs).
- **`scenario/`** — `Scenario` (interface), `SimpleRouteScenario` (implementado), `ScenarioFactory` (stub).
- **`data/`** — `TrainRepository` (6 trenes: 4 OpenTTD + Renfe 592 Camello + Talgo 350), `RouteRepository` (C-1 Murcia–Alicante 11 estaciones + Madrid–Guadalajara 2 estaciones), `ScenarioRepository` (2 escenarios predefinidos).
- **`ui/`** — `ConsoleMenu` (object stateless, menú de texto genérico).

---

## Convenciones del proyecto

- **Unidades internas siempre en SI** (m, m/s, N, kg). Las conversiones solo en getters de presentación o en los repositorios al definir valores (`200.0 / 3.6`).
- **`grade` siempre en permil (‰)**, no en porcentaje.
- `data class` para value objects (`TrackSegment`, `DriveCommand`, `SimulationConfig`, `DrivingContext`). `class` para servicios con comportamiento o dependencias inyectables. `object` solo para utilidades sin estado (`PhysicsEngine`, `ConsoleMenu`).
- `Station` es un value object con posiciones **relativas** al stop (`approachDistance`, `departureDistance` en metros, ≥ 0). `RouteEntry` la sitúa en la ruta con una `position` absoluta (metros desde origen) y deriva `approachPoint = position − approachDistance` y `departurePoint = position + departureDistance`. `stopTolerance` (default 50 m) define el margen ±m de parada válida.
- `DrivingContext` se construye cada paso via `Route.drivingContextAt(position)`. Contiene el `TrackSegment` activo, distancia/velocidad de aproximación a la próxima estación, y tres flags de zona (`isInApproachZone`, `isInDepartureZone`, `isInStopZone`) calculados por `RouteEntry`. Es el único contexto que recibe `Driver.drive()`.
- `RouteEntry.position` es `Int` (metros enteros). La búsqueda en `drivingContextAt` hace `it.position <= position.toInt()` — precaución en tests cerca de límites enteros.

---

## AutopilotDriver — máquina de estados

Cinco fases (enum `Phase`):

| Fase | throttle | brake | Condición de salida |
|------|----------|-------|---------------------|
| STOPPED | 0 | 1.0 | siempre → ACCELERATING |
| ACCELERATING | 1.0 | 0 | v ≥ targetCruisingSpeed → CRUISING |
| CRUISING | Kp × (target−v) ∈ [0,1] | 0 | d ≤ FINAL\_BRAKE\_DISTANCE, ó (d ≤ BRAKE\_DISTANCE AND v > nextStopApproachSpeed) → BRAKING |
| BRAKING *(un único estado enum con dos sub-comportamientos según distancia)* | | | |
| — sub: approach (d > FINAL) | 0 | 0.5 | v ≤ nextStopApproachSpeed → CRUISING |
| — sub: final (d ≤ FINAL) | 0 | v/maxSpeed | isInStopZone AND v ≤ STOP\_VELOCITY → STOPPED |
| COASTING | 0 | 0 | reservado, no usado aún |

Constantes relevantes: `BRAKE_DISTANCE = 5000 m`, `FINAL_BRAKE_DISTANCE = 100 m`, `KP = 0.5`, `STOP_VELOCITY = 0.5 m/s`.

En CRUISING: target = `nextStopApproachSpeed` si `isInApproachZone`, si no `min(lineSpeedLimit, maxSpeed)`.

---

## Tests

Estrategia: **Boundary Value Analysis (BVA)**. Ficheros existentes:
- `model/RouteTest.kt` — 1 test implementado (`segmentAtReturnsNullAtTerminalStation`), 8 TODOs BVA para casos frontera de `segmentAt`.
- `physics/PhysicsEngineTest.kt` — stub vacío.

---

## TODOs conocidos

Ver también `Autopilot.md` para el diseño del autopiloto.

- `CsvExporter` y `ArrivalChecker` — stubs: implementar como `SimulationObserver`.
- `ScenarioFactory` — stub: factory para combinar tren + escenario predefinido.
- Especialización de `Station` para terminales (sin approachPoint/departurePoint en extremos).
- `TrackSegment` dinámico con pendiente y velocidad máxima variables por posición.
- Tiempo de parada en estación (dwell time) — `AutopilotDriver` transiciona STOPPED→ACCELERATING inmediatamente.
- Control de velocidad de simulación (`realTimeFactor`) con corutinas.
- Terminal interactiva con input no bloqueante (toggle autopiloto/manual, W/S/B).
- Visualización ASCII de posición del tren en la ruta.
- `TrainSnapshot` inmutable para pasar a observers en lugar de la referencia mutable de `Train`.
- Completar tests BVA en `RouteTest` y escribir `PhysicsEngineTest`.
