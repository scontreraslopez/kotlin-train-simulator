# Ejercicios UML — Entornos de Desarrollo (EDE)

**Proyecto de referencia:** `kotlin-train-simulator`
**Instrucciones generales:** Todos los diagramas deben seguir la notación UML 2.x estándar. Podéis utilizar cualquier herramienta (draw.io, StarUML, PlantUML, Lucidchart, papel y escáner, etc.). Indicad claramente nombres, tipos, multiplicidades y estereotipos donde corresponda.

---

## Ejercicio 1 — Diagrama de Estados: `AutopilotDriver`

### Contexto

La clase `AutopilotDriver` implementa un piloto automático para el simulador de trenes. Internamente gestiona una máquina de estados con **cinco fases** definidas en el enum `Phase`: `STOPPED`, `ACCELERATING`, `CRUISING`, `BRAKING` y `COASTING`.

En cada paso de simulación se invoca el método `drive(train, context)`, que evalúa el estado actual del tren (velocidad, posición) y el contexto de conducción (distancia a la próxima parada, zonas de aproximación/parada, límite de velocidad del tramo) para decidir la siguiente fase y generar un comando de conducción (`DriveCommand` con valores de `throttle` y `brake` entre 0 y 1).

### Información que necesitáis

A continuación se describen las **transiciones** entre fases y las **condiciones de guarda** que las disparan:

| Fase origen | Fase destino | Condición de guarda |
|---|---|---|
| `STOPPED` | `ACCELERATING` | Siempre (transición inmediata) |
| `ACCELERATING` | `CRUISING` | `velocity >= targetCruisingSpeed` |
| `CRUISING` | `BRAKING` | `distanceToNextStop <= FINAL_BRAKE_DISTANCE (100 m)` |
| `CRUISING` | `BRAKING` | `distanceToNextStop <= BRAKE_DISTANCE (5000 m) AND velocity > nextStopApproachSpeed` |
| `BRAKING` | `STOPPED` | `isInStopZone AND velocity <= STOP_VELOCITY (0.5 m/s)` |
| `BRAKING` | `CRUISING` | `distanceToNextStop > FINAL_BRAKE_DISTANCE AND velocity <= nextStopApproachSpeed` |

> **Nota:** `COASTING` está definido en el enum pero no se utiliza actualmente. Debéis incluirlo en el diagrama como un estado sin transiciones entrantes (estado reservado para uso futuro).

Adicionalmente, cada fase produce una **acción interna** (el comando de conducción):

| Fase | Acción (do / entry) |
|---|---|
| `STOPPED` | `throttle = 0, brake = 1.0` |
| `ACCELERATING` | `throttle = 1.0, brake = 0` |
| `CRUISING` | `throttle = Kp × (targetSpeed − velocity), brake = 0` |
| `BRAKING` (distancia > 100 m) | `throttle = 0, brake = 0.5` |
| `BRAKING` (distancia ≤ 100 m) | `throttle = 0, brake = velocity / maxSpeed` |

### Se pide

1. Dibujad un **diagrama de estados UML** completo para `AutopilotDriver`.
2. Incluir:
   - **Estado inicial** (pseudoestado) con transición al estado `STOPPED`.
   - Los **cinco estados** (`STOPPED`, `ACCELERATING`, `CRUISING`, `BRAKING`, `COASTING`).
   - Todas las **transiciones** con sus condiciones de guarda entre corchetes `[condición]`.
   - Las **acciones internas** de cada estado (usando la notación `do /` o `entry /`).
   - `BRAKING` tiene **dos sub-comportamientos** según la distancia: indicadlo mediante un estado compuesto con dos sub-estados internos (`ApproachBraking` y `FinalBraking`) o bien mediante notas UML que aclaren el comportamiento diferenciado.
   - `COASTING` como estado aislado con una nota indicando «reservado para uso futuro».

---

## Ejercicio 2 — Diagrama de Clases: paquete `model`

### Contexto

El paquete `model` contiene las clases que representan el dominio del simulador: trenes, estaciones, tramos de vía, entradas de ruta, rutas y el contexto de conducción. Todas las magnitudes se almacenan internamente en unidades del SI (metros, m/s, kg, N, W).

### Nota previa: `data class` en Kotlin

En Kotlin existe la palabra clave `data class`. Cuando una clase se declara como `data class`, el compilador genera automáticamente los métodos `equals()`, `hashCode()`, `toString()` y `copy()` basándose en los atributos declarados en el constructor primario. Es el equivalente a lo que en Java hacíais a mano (o delegabais en vuestra IDE), y es muy similar a los **records** introducidos en Java 16 (`public record Punto(int x, int y) {}`), con la diferencia de que en Kotlin los atributos pueden ser mutables (`var`) y la clase puede tener herencia.

A efectos del diagrama UML no hay ninguna diferencia estructural respecto a una clase normal: se representa exactamente igual, con sus atributos, métodos y relaciones. Simplemente añadimos el estereotipo `«dataclass»` en el nombre, del mismo modo que usaríais `«interface»` o `«abstract»` para indicar esa característica. El diagrama no cambia en nada más.

### Clases del paquete

A continuación se listan las **seis clases** del paquete `model` con sus atributos y métodos relevantes:

**`Train`** (clase regular — estado mutable)
- Atributos: `name: String`, `position: Double`, `velocity: Double`, `mass: Double`, `maxSpeed: Double`, `maxPower: Double`, `maxTractiveEffort: Double`, `maxBrakingForce: Double`, `davisA: Double`, `davisB: Double`, `davisC: Double`, `rotatingMassFactor: Double`
- Métodos: `velocityKmH(): Double`, `toString(): String`
- Nota: `position` y `velocity` son mutables; el resto son inmutables.

**`Station`** (data class)
- Atributos: `name: String`, `approachDistance: Int`, `departureDistance: Int`, `maxApproachSpeed: Double`, `maxDepartureSpeed: Double`, `stopTolerance: Int`
- Métodos: `maxApproachSpeedKmH(): Double`, `maxDepartureSpeedKmH(): Double`, `approachDistanceKm(): Double`, `departureDistanceKm(): Double`

**`TrackSegment`** (data class)
- Atributos: `grade: Double`, `lineSpeedLimit: Double`
- Métodos: `lineSpeedLimitKmH(): Double`

**`RouteEntry`** (data class)
- Atributos: `station: Station`, `position: Int`, `segmentToNext: TrackSegment?`
- Propiedades derivadas: `approachPoint: Int` (get), `departurePoint: Int` (get)
- Métodos: `isInApproachZone(trainPosition: Double): Boolean`, `isInDepartureZone(trainPosition: Double): Boolean`, `hasReachedStop(trainPosition: Double): Boolean`, `isInStopZone(trainPosition: Double): Boolean`

**`Route`** (data class)
- Atributos: `routeEntries: List<RouteEntry>`
- Métodos: `segmentAt(position: Double): TrackSegment?`, `drivingContextAt(position: Double): DrivingContext?`

**`DrivingContext`** (data class)
- Atributos: `segment: TrackSegment`, `distanceToNextStop: Double`, `nextStopApproachSpeed: Double`, `isInApproachZone: Boolean`, `isInDepartureZone: Boolean`, `isInStopZone: Boolean`

### Se pide

1. Dibujad un **diagrama de clases UML** del paquete `model`.
2. Incluir para cada clase:
   - Nombre de la clase con el estereotipo adecuado: `«dataclass»` para las data classes, sin estereotipo para `Train`.
   - **Atributos** con visibilidad (`+`/`-`/`#`), nombre y tipo. Marcar los atributos mutables con el indicador de lectura/escritura donde aplique.
   - **Métodos** con visibilidad, nombre, parámetros y tipo de retorno.
3. Indicar las **relaciones** entre clases:
   - **Composición** (rombo negro): `Route` contiene `RouteEntry` (multiplicidad `1..*`).
   - **Asociación**: `RouteEntry` tiene una `Station` (multiplicidad `1`) y un `TrackSegment` opcional (multiplicidad `0..1`).
   - **Asociación**: `DrivingContext` tiene un `TrackSegment` (multiplicidad `1`).
   - **Dependencia** (línea discontinua): `Route` crea/devuelve `DrivingContext` (en el método `drivingContextAt`).
4. Indicad las **multiplicidades** en todos los extremos de las relaciones.

---

## Ejercicio 3 — Diagrama de Secuencia: consulta de escenarios desde los repositorios

### Contexto

El simulador utiliza tres repositorios para obtener los datos del dominio:
- **`TrainRepository`**: proporciona instancias de `Train` mediante métodos factory (ej.: `renfe592Camello()`, `all()`).
- **`RouteRepository`**: expone rutas como propiedades (ej.: `cercaniasMurciaAlicanteC1: Route`).
- **`ScenarioRepository`**: recibe un `RouteRepository` por inyección de dependencias y crea escenarios combinando rutas con descripciones (ej.: `cercaniasMurciaAlicante(): Scenario`).

La secuencia que debéis modelar es la siguiente: un objeto **`Main`** (el punto de entrada de la aplicación) quiere obtener un tren y un escenario para iniciar una simulación. Para ello:

1. `Main` crea una instancia de `TrainRepository`.
2. `Main` llama a `trainRepository.renfe592Camello()` y recibe un objeto `Train`.
3. `Main` crea una instancia de `RouteRepository`.
4. `Main` crea una instancia de `ScenarioRepository` pasándole el `routeRepository` como parámetro del constructor.
5. `Main` llama a `scenarioRepository.cercaniasMurciaAlicante()`.
6. Internamente, `ScenarioRepository` accede a `routeRepository.cercaniasMurciaAlicanteC1` para obtener la `Route`.
7. `ScenarioRepository` crea un `SimpleRouteScenario` con la descripción y la ruta, y lo devuelve a `Main` como `Scenario`.

### Se pide

1. Dibujad un **diagrama de secuencia UML** que represente la interacción descrita arriba.
2. Incluir:
   - **Líneas de vida** (lifelines) para: `Main`, `TrainRepository`, `RouteRepository`, `ScenarioRepository`.
   - Mensajes de **creación** de objetos (estereotipo `«create»` o flecha discontinua al rectángulo de activación).
   - Mensajes **síncronos** (flechas con punta rellena) para las llamadas a métodos.
   - Mensajes de **retorno** (flechas discontinuas) indicando el objeto devuelto (ej.: `: Train`, `: Scenario`).
   - Un **fragmento de interacción** (marco `ref` o `alt`) no es necesario en este caso, pero si queréis representar la llamada interna de `ScenarioRepository` a `RouteRepository`, podéis usar un **marco de activación anidado**.
   - Indicad claramente el **orden temporal** de los mensajes (numeración opcional).

---

## Criterios de evaluación (comunes a los tres ejercicios)

| Criterio | Peso |
|---|---|
| Corrección de la notación UML | 30% |
| Completitud (todos los elementos solicitados presentes) | 30% |
| Corrección del contenido (refleja fielmente el código fuente) | 25% |
| Claridad y legibilidad del diagrama | 15% |
