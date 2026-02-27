# Kotlin Train Simulator

## Qué es y para qué sirve

Este proyecto es un simulador de dinámica ferroviaria escrito en Kotlin. Modela un tren que recorre una ruta con estaciones, aplicando física newtoniana real: tracción, frenado, resistencia aerodinámica (ecuación de Davis) y efecto de pendientes.

El tren puede ser conducido manualmente (comando fijo) o por un autopiloto que implementa una máquina de estados con cinco fases: parado, acelerando, velocidad de crucero, rodando por inercia y frenando. El autopiloto decide en cada instante cuánto throttle y cuánto freno aplicar a partir de la posición del tren, su velocidad y las restricciones de la vía.

**¿Por qué un simulador de trenes para enseñar ingeniería del software?**

Un tren es un dominio que todo el mundo entiende — tiene velocidad, posición, estaciones, rutas — pero su modelado toca problemas reales de diseño: separar la física del control, intercambiar estrategias de conducción, observar sin interferir, modelar datos inmutables frente a estado mutable. Todo esto hace que sea un terreno ideal para practicar UML, testing, documentación y refactoring sobre un ejemplo concreto y tangible.

---

## Cómo ejecutar

```bash
./gradlew run          # ejecuta Main.kt (menú interactivo)
./gradlew build        # compila
./gradlew test         # lanza tests (JUnit 5)
./gradlew test --tests "io.github.scontreraslopez.trainsim.SomeTest"  # test individual
```

Kotlin 2.2.10, JVM toolchain 21. Dependencias de test: `junit-jupiter:5.14.3` + `junit-platform-launcher:1.14.3`.

---

## Arquitectura y flujo de datos

El simulador ejecuta un bucle temporal. En cada paso (`dt = 0.1 s` por defecto) los datos fluyen en una sola dirección, de arriba abajo, sin retroalimentación:

```
Route.drivingContextAt(position)
    ↓
DrivingContext (valor inmutable: segmento activo, distancia al stop, zona)
    ↓
Driver.drive(train, context)
    ↓
DriveCommand (valor inmutable: throttle ∈ [0,1], brake ∈ [0,1])
    ↓
PhysicsEngine.step(train, command, segment, dt)
    ↓
Train (mutado: nueva posición y velocidad)
    ↓
SimulationObserver.onStep(train, command, context, time)
```

**¿Por qué unidireccional?** Porque cada componente recibe lo que necesita y produce lo que le toca sin efectos laterales inesperados. La ruta no sabe que existe un conductor, el conductor no sabe que existe un motor físico, y los observadores no pueden modificar el tren. Esto hace que cada pieza sea testeable de forma aislada y que el flujo sea predecible — dado un estado inicial, la simulación siempre produce el mismo resultado.

`Simulator` orquesta este bucle y para cuando el escenario indica que el tren ha completado la ruta o se agota el tiempo máximo configurado.

---

## Patrones de diseño

El codebase usa varios patrones de diseño clásicos. Lo interesante no es solo identificarlos sino entender *por qué* se eligió cada uno frente a la alternativa.

### Strategy — `Driver` y `Scenario`

**Dónde:** `Driver` es una interfaz con un único método `drive(train, context): DriveCommand`. `ManualDriver` y `AutopilotDriver` son implementaciones concretas. Lo mismo ocurre con `Scenario`: `SimpleRouteScenario` define una ruta y su condición de fin.

**Por qué:** El simulador no debe saber si quien conduce es un humano, un autopiloto o un algoritmo de aprendizaje automático. Al definir `Driver` como interfaz, podemos cambiar la estrategia de conducción en runtime (desde el menú de consola) sin tocar `Simulator`. Lo mismo con los escenarios.

**Alternativa:** Hardcodear la lógica de conducción dentro de `Simulator`. Funcionaría, pero cada nueva estrategia requeriría modificar el simulador — violación del principio abierto/cerrado (OCP).

### Observer — `SimulationObserver`

**Dónde:** `SimulationObserver` es una interfaz con `onStep(...)`. `ConsoleLogger` la implementa para imprimir el estado por consola. `CsvExporter` y `ArrivalChecker` están diseñados como stubs para exportar a CSV y validar paradas respectivamente.

**Por qué:** El simulador necesita informar de lo que ocurre (logging, exportación, validación) sin acoplarse a cómo se usa esa información. Al usar observadores, se pueden añadir nuevas salidas (JSON, gráficas, alarmas) sin modificar el bucle de simulación. Además, los observadores reciben datos pero no pueden modificar el tren — la observación no afecta al experimento.

**Alternativa:** Meter `println` directamente dentro de `Simulator.run()`. Funcionaría, pero cada nueva necesidad de logging requeriría modificar el código del simulador.

### State Machine — `AutopilotDriver.Phase`

**Dónde:** `AutopilotDriver` define un `enum class Phase` anidado con cinco estados: `STOPPED`, `ACCELERATING`, `CRUISING`, `COASTING`, `BRAKING`. En cada paso, el autopiloto primero evalúa si debe transicionar de estado y luego emite el `DriveCommand` correspondiente al estado actual.

**Por qué:** La conducción de un tren tiene fases claramente diferenciadas — no tiene sentido frenar y acelerar a la vez. Una máquina de estados hace explícitas las transiciones (cuándo pasa de acelerar a crucero, cuándo empieza a frenar) y cada estado tiene una acción clara y testeable.

**Alternativa:** Un bloque monolítico de `if/else` que evalúe todas las condiciones a la vez. Funcionaría para 3 condiciones, pero con 5 estados y múltiples transiciones se convierte en código ilegible y propenso a errores.

El diseño completo de la máquina de estados está documentado en `Autopilot.md`.

### Repository — `TrainRepository`, `RouteRepository`, `ScenarioRepository`

**Dónde:** En el paquete `data/`. Cada repositorio es una clase que actúa como catálogo de entidades predefinidas. `TrainRepository` contiene 6 trenes (4 de OpenTTD + 2 de Renfe) con sus parámetros físicos reales. `RouteRepository` define la línea C-1 Murcia–Alicante con 11 estaciones.

**Por qué:** Los datos de configuración (qué trenes existen, qué rutas hay) se centralizan en un solo lugar y se separan de la lógica. Esto evita que `Main.kt` o `Simulator` contengan bloques de datos hardcodeados entre la lógica de negocio. Cada repositorio es una fuente de verdad para su dominio.

**Alternativa:** Definir los trenes y rutas directamente en `Main.kt`. Funcionaría, pero mezclaría datos con cableado y haría el código de arranque ilegible.

### Factory — `SimulationFactory`

**Dónde:** `SimulationFactory` ensambla un `Simulator` completo a partir de un tren y un escenario, proporcionando defaults para la configuración, el conductor y los observadores.

**Por qué:** Crear un `Simulator` requiere 5 dependencias. Sin factory, cada llamada en `Main.kt` tendría que especificarlas todas. La factory encapsula las decisiones por defecto (autopiloto, logger a consola, dt=0.1s) y deja que el cliente solo elija lo que le importa.

### Value Object — `Station`, `TrackSegment`, `DrivingContext`, `DriveCommand`, `SimulationConfig`

**Dónde:** Todas son `data class` inmutables. Se comparan por valor, se pueden copiar con `copy()`, y nunca cambian después de crearse.

**Por qué:** Un `DriveCommand(throttle=0.8, brake=0.0)` es un valor, igual que el número 42. No tiene identidad propia — dos comandos con los mismos valores son intercambiables. Al hacerlos `data class`, Kotlin genera automáticamente `equals()`, `hashCode()` y `copy()` basados en sus propiedades, lo cual es exactamente la semántica que queremos.

**Contraste:** `Train` **no** es `data class` porque tiene estado mutable (posición, velocidad) y comportamiento. Dos trenes con la misma posición y velocidad no son el mismo tren — tienen identidad propia.

### Singleton — `PhysicsEngine`, `ConsoleMenu`

**Dónde:** Ambos usan la keyword `object` de Kotlin, que garantiza una única instancia global.

**Por qué:** `PhysicsEngine` es una colección de funciones puras (sin estado, sin efectos laterales salvo la mutación del tren que recibe). No tiene sentido instanciarlo — no hay nada que configurar. Lo mismo aplica a `ConsoleMenu`, que es una utilidad genérica de UI sin estado propio. `object` en Kotlin elimina la ceremonia de crear una instancia o implementar el patrón singleton manualmente.

### Composition Root — `Main.kt`

**Dónde:** La función `main()` crea los repositorios, presenta el menú, y cablea todas las dependencias.

**Por qué:** Todas las decisiones de *qué implementación usar* se toman en un solo sitio. `Simulator` no sabe que existe `TrainRepository`; solo recibe un `Train`. `ConsoleMenu` no sabe que existe `Scenario`; solo muestra opciones y devuelve un índice. Este desacoplamiento hace que cada clase sea testeable de forma independiente.

---

## Convenciones del proyecto

- **Unidades internas siempre en SI** (m, m/s, N, kg). Las conversiones a km/h o km solo se hacen en getters de presentación (`velocityKmH()`, `approachDistanceKm()`) o al definir valores en los repositorios (`200.0 / 3.6`).
- **`grade` siempre en permil (‰)**, no en porcentaje. Anotado en todos los sitios donde aparece.
- **`data class`** para value objects (inmutables, sin identidad). **`class`** para servicios con comportamiento o entidades con estado mutable. **`object`** exclusivamente para singletons sin estado ni dependencias externas.
- **Inyección de dependencias por constructor** — sin framework, sin anotaciones. Cada clase recibe lo que necesita en el constructor y no busca dependencias por su cuenta.
- **Validación en `init`** — las precondiciones del dominio se validan en el bloque `init` con `require()`. Si una estación tiene `approachDistance < 0`, falla al instanciar, no al simular.

---

## Física del modelo

### Ecuación de movimiento

```
a = F_neta / (m · ξ)

F_neta = F_tracción - F_frenado - F_resistencia - F_rampa
```

| Símbolo | Descripción                    | Unidades |
|---------|-------------------------------|----------|
| `m`     | Masa del tren                 | kg       |
| `ξ`     | Factor de masa rotante        | adim.    |
| `a`     | Aceleración                   | m/s²     |

### Hipótesis y simplificaciones

Cada hipótesis incluye la fórmula usada, la alternativa más realista que se descartó, y por qué la simplificación es defendible.

#### H1 — Integración numérica: Euler semi-implícito

```
v(t+dt) = v(t) + a(t) · dt      ← actualiza velocidad primero
x(t+dt) = x(t) + v(t+dt) · dt   ← usa velocidad ya actualizada
```

**Alternativa:** RK4 con error O(dt⁴). **Rationale:** Con `dt ≤ 0.5 s` el error es despreciable para distancias ferroviarias.

#### H2 — Tracción: límite por potencia y esfuerzo máximo

```
F_t = min(F_t_max, P_max / max(v, ε)) × throttle
```

**Alternativa:** Lookup table real del vehículo. **Rationale:** La curva hiperbólica captura zona de par constante / potencia constante con solo dos parámetros.

#### H3 — Frenado: fuerza proporcional

```
F_b = brake × F_b_max     brake ∈ [0.0, 1.0]
```

**Alternativa:** Modelo neumático con dinámica de tubería. **Rationale:** El objetivo es evaluar *cuándo* frenar, no *cómo* funciona el freno internamente.

#### H4 — Resistencia al avance: ecuación de Davis

```
F_r = A + B·v + C·v²
```

| `A` | Rodadura (cojinetes, carril) | `B·v` | Rozamiento de pestaña | `C·v²` | Arrastre aerodinámico |
|-----|------------------------------|-------|-----------------------|--------|-----------------------|

**Alternativa:** Separar `½ρCdAv²` con datos de túnel de viento. **Rationale:** Davis es el estándar industrial desde 1926.

#### H5 — Rampa: pendiente pequeña

```
F_g = m · g · (grade‰ / 1000)
```

**Alternativa:** `sin(θ)` exacto. **Rationale:** Error < 0.1 % para rampas ferroviarias convencionales (< 35 ‰).

#### H6 — Factor de masa rotante: constante

```
ξ = 1.06
```

**Alternativa:** Calcular `ξ` por momentos de inercia. **Rationale:** La incertidumbre en la masa del tren domina sobre la variación de `ξ`.

#### H7 — Condiciones de vía: `TrackSegment` estático por tramo

Cada tramo entre estaciones tiene `grade` y `lineSpeedLimit` constantes. `Route.drivingContextAt(position)` construye un `DrivingContext` por paso.

**Alternativa:** Perfil altimétrico real con pendiente variable. **Rationale:** Un segmento uniforme permite validar el motor de física antes de añadir complejidad.

---

## Uso didáctico del codebase

Este proyecto está diseñado para servir como base de prácticas en varias disciplinas de ingeniería del software:

### UML

El codebase contiene material para los cuatro tipos principales de diagramas UML:

- **Diagrama de clases** — el paquete `model/` contiene composición (`Route` → `RouteEntry`), asociación (`RouteEntry` → `Station`), y `data class` vs `class`. Las interfaces `Driver`, `Scenario` y `SimulationObserver` muestran herencia de interfaz con implementaciones concretas. El paquete completo tiene 25 clases/interfaces/objetos con relaciones no triviales.
- **Diagrama de secuencia** — el bucle de `Simulator.run()` es un ejemplo limpio: `Simulator` → `Route` → `Driver` → `PhysicsEngine` → `Observer`. Un paso completo de simulación involucra 5 objetos con mensajes síncronos claros.
- **Diagrama de estados** — `AutopilotDriver.Phase` tiene 5 estados con transiciones documentadas en `Autopilot.md`. Las condiciones de guarda (velocidad, distancia) son explícitas.
- **Diagrama de actividad** — `Simulator.run()` con su condición de parada doble (escenario completado O tiempo agotado) y los pasos internos.

### Testing

Qué es fácil de testear (y por qué):
- **`PhysicsEngine`** — es un `object` sin estado. Se le pasan datos, devuelve resultados. Test unitario puro: `computeGradeForce(train, 20.0)` debe devolver el valor esperado.
- **`AutopilotDriver`** — la máquina de estados tiene transiciones verificables: crear un tren a velocidad X con distancia Y al stop y comprobar qué `Phase` y qué `DriveCommand` produce.
- **Validaciones `init`** — `Station(approachDistance = -1, ...)` debe lanzar `IllegalArgumentException`. Tests de contrato.

Qué es difícil de testear (y por qué es interesante como ejercicio):
- **`Simulator`** — requiere montar un `Train`, un `Driver`, un `Scenario` y observadores. Buen ejercicio de test de integración.
- **`ConsoleLogger`** — imprime a `stdout`. Requiere captura de salida o inyección de un `OutputHandler`.
- **`ConsoleMenu`** — lee de `stdin`. Requiere inyección de un `InputProvider`.

### Documentación

- **KDoc** presente en todas las clases del modelo con `@property` y explicaciones de dominio.
- **Hipótesis H1-H7** — ejemplo de documentación técnica que justifica decisiones con fórmulas, alternativas y rationale.
- **`Autopilot.md`** — ejemplo de documento de diseño escrito *antes* de implementar, con diagrama de transiciones y parámetros por determinar.

### Refactoring

El codebase tiene oportunidades de refactoring diseñadas como ejercicio:
- **Separar `TrainSpec` de `Train`** — actualmente `Train` mezcla parámetros físicos inmutables (masa, potencia, Davis) con estado mutable (posición, velocidad). Separar en `TrainSpec` (catálogo) + `Train` (estado) mejora la semántica y permite que un mismo spec cree múltiples trenes.
- **`TrainSnapshot` inmutable para observers** — actualmente los observers reciben una referencia al `Train` mutable. Un observer mal implementado podría mutar el tren. Refactorizar a `TrainSnapshot` (data class) elimina ese riesgo.
- **`InputProvider` / `OutputHandler`** — extraer IO de `ConsoleMenu` a interfaces inyectables para hacer la lógica de menú testeable sin consola real.

---

## Estado actual

### Implementado

- **Motor físico** — `PhysicsEngine`: Davis, tracción hiperbólica, Euler semi-implícito, clamp de velocidad.
- **Modelo de dominio** — `Train`, `Station`, `Route`, `RouteEntry`, `TrackSegment`, `DrivingContext`.
- **Conducción** — `Driver` (interface), `DriveCommand`, `ManualDriver`, `AutopilotDriver` (máquina de estados: STOPPED / ACCELERATING / CRUISING / COASTING / BRAKING). Diseño en `Autopilot.md`.
- **Simulación** — `Simulator` (bucle principal), `SimulationConfig`, `SimulationFactory`.
- **Catálogos** — `TrainRepository` (4 trenes OpenTTD + Renfe 592 Camello + S-102 Talgo 350), `RouteRepository` (C-1 Murcia–Alicante, 11 estaciones), `ScenarioRepository`.
- **Observadores** — `SimulationObserver` (interface), `ConsoleLogger`.
- **UI** — `ConsoleMenu` (menú interactivo de selección de escenario y tren).

### Pendiente (stubs documentados)

- **`ArrivalChecker`** — observer que detecta paradas válidas (±`stopTolerance`) y calcula desviación respecto al `stopPoint`.
- **`CsvExporter`** — observer que vuelca cada paso a CSV para análisis externo (Excel, Python...).
- **`ScenarioFactory`** — fábrica de escenarios predefinidos listos para usar desde `Main`.

---

## TODO / Mejoras pendientes

### Tests y calidad de código

- **Tests unitarios** — prioritarios:
  - `Route.drivingContextAt`: casos frontera (posición en stopPoint, última estación, fuera de ruta).
  - `PhysicsEngine`: consistencia de fuerzas (tracción, Davis, rampa), integración a velocidad cero.
  - `SimulationConfig`: validación de `timeStep`.
- **GitHub Actions CI** — workflow que ejecute `./gradlew build` y `./gradlew test` en cada push.
- **ktlint / detekt** — análisis estático y formateo automático en el build.
- ~~**LICENSE**~~ — ✓ MIT añadida.

### Modelo de dominio

- **Separar `TrainSpec` de `Train`** — `TrainSpec` como parámetros físicos inmutables (catálogo), `Train` como estado mutable de simulación.
- **Especialización de estaciones terminales** — `approachDistance = 0` / `departureDistance = 0` ya funciona; podría hacerse explícito con subtipos.
- **`TrackSegment` dinámico** — pendiente y límite de velocidad variables por posición dentro del tramo.
- **`TrainSnapshot` inmutable** — value object que los observers reciben en lugar del `Train` mutable.

### Testabilidad y arquitectura

- **`InputProvider` / `OutputHandler`** — extraer IO de `ConsoleMenu` a interfaces inyectables.
- **Argumentos CLI** — modo batch: seleccionar tren y escenario por argumento sin interacción.

### UX y tiempo real

- **Velocidad en km/h en `ConsoleLogger`** — usar `train.velocityKmH()` en lugar de m/s.
- **Visualización ASCII** — barra de progreso con posición del tren y estaciones.
- **`realTimeFactor`** — multiplicador de velocidad de simulación (×1, ×10, ×100) con corutinas.
- **Terminal interactiva** — input no bloqueante: `W`/`S` throttle, `B` freno de emergencia, `A` toggle autopiloto/manual.

---

## Estructura del proyecto

```
model/       → Train, Station, Route, RouteEntry, TrackSegment, DrivingContext
               Entidades del dominio. Value objects (data class) + agregados.

physics/     → PhysicsEngine (object)
               Motor de física sin estado. Davis + tracción + Euler semi-implícito.

control/     → Driver (interface), DriveCommand, ManualDriver, AutopilotDriver
               Patrón Strategy para conducción. Máquina de estados en AutopilotDriver.

simulation/  → Simulator, SimulationConfig, SimulationFactory, Scenario (interface)
               Bucle principal + Factory + configuración.

observer/    → SimulationObserver (interface), ConsoleLogger, CsvExporter*, ArrivalChecker*
               Patrón Observer para desacoplar logging/exportación de la simulación.

scenario/    → SimpleRouteScenario, ScenarioFactory*
               Implementaciones concretas de Scenario (Strategy).

data/        → TrainRepository, RouteRepository, ScenarioRepository
               Patrón Repository. Catálogos de datos (trenes OpenTTD/Renfe, rutas).

ui/          → ConsoleMenu (object)
               Menú genérico de consola. No sabe de trenes ni rutas.

Main.kt      → Composition Root. Crea repos, cablea dependencias, arranca simulación.

* stub documentado, pendiente de implementar
```
