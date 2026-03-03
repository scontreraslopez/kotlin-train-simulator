# Ejercicios de Diseño de Pruebas (I) — Entornos de Desarrollo (EDE)

**Proyecto de referencia:** `kotlin-train-simulator`
**Contenidos evaluados:** Clases de equivalencia (EP), Análisis de valores límite (BVA), casos de prueba formales.

---

## Ejercicio 1 — Clases de equivalencia y valores límite: constructor de `Station`

### Contexto

En el simulador de trenes, la clase `Station` representa una estación ferroviaria. Su constructor valida los parámetros de entrada con las siguientes reglas:

```kotlin
data class Station(
    val name: String,
    val approachDistance: Int,      // metros antes del punto de parada
    val departureDistance: Int,     // metros después del punto de parada
    val maxApproachSpeed: Double,   // velocidad máxima de aproximación (m/s)
    val maxDepartureSpeed: Double,  // velocidad máxima de salida (m/s)
    val stopTolerance: Int = 50     // margen de parada válida (metros)
) {
    init {
        require(approachDistance >= 0)  { "approachDistance debe ser no negativa" }
        require(departureDistance >= 0) { "departureDistance debe ser no negativa" }
        require(maxApproachSpeed > 0)   { "maxApproachSpeed debe ser positiva" }
        require(maxDepartureSpeed > 0)  { "maxDepartureSpeed debe ser positiva" }
        require(stopTolerance > 0)      { "stopTolerance debe ser positiva" }
    }
}
```

> **Nota sobre `require`:** En Kotlin, `require(condición)` lanza una `IllegalArgumentException` si la condición es `false`. Es equivalente al patrón de Java:
> ```java
> if (!condición) throw new IllegalArgumentException("mensaje");
> ```

Observad que hay **dos tipos de validación** distintos en este constructor:

- Parámetros que admiten el cero: `approachDistance >= 0`, `departureDistance >= 0`
- Parámetros que **no** admiten el cero: `maxApproachSpeed > 0`, `maxDepartureSpeed > 0`, `stopTolerance > 0`

### Se pide

#### Apartado A — Clases de equivalencia

Centraos en los parámetros `approachDistance` y `maxApproachSpeed`. Para cada uno:

1. Identificad las **clases de equivalencia** (válidas y no válidas).
2. Elegid un **valor representativo** de cada clase.
3. Completad la siguiente tabla (una fila por clase de equivalencia):

| Parámetro | Clase | Rango | Tipo (válida / no válida) | Valor representativo |
|-----------|-------|-------|---------------------------|----------------------|
| `approachDistance` | CE-1 | | | |
| `approachDistance` | CE-2 | | | |
| `maxApproachSpeed` | CE-3 | | | |
| `maxApproachSpeed` | CE-4 | | | |

> **Pista:** Prestad atención a la diferencia entre `>= 0` y `> 0`. ¿En cuántas clases se divide cada uno? ¿El cero pertenece a la clase válida o a la no válida en cada caso?

#### Apartado B — Análisis de valores límite

Ahora aplicad BVA a los mismos dos parámetros. Completad la siguiente tabla con los valores de frontera y el resultado esperado:

**Para `approachDistance` (condición: `>= 0`, tipo `Int`):**

| Valor | Descripción | Resultado esperado |
|-------|-------------|--------------------|
| | Justo fuera del límite inferior | |
| | Exactamente en el límite | |
| | Justo dentro del límite | |

**Para `maxApproachSpeed` (condición: `> 0`, tipo `Double`):**

| Valor | Descripción | Resultado esperado |
|-------|-------------|--------------------|
| | Valor negativo cercano a cero | |
| | Exactamente en el límite | |
| | Valor positivo cercano a cero | |

> **Pista:** `approachDistance` es `Int` (entero), así que "justo fuera" y "justo dentro" difieren en 1 unidad. `maxApproachSpeed` es `Double` (decimal), así que podéis usar un valor pequeño como `0.001` para "justo dentro".

## Ejercicio 2 — Casos de prueba formales: `RouteEntry.isInStopZone()`

### Contexto

En el simulador, cuando un tren se acerca a una estación, el sistema necesita determinar si el tren está dentro de la **zona de parada válida**. El método `isInStopZone` de la clase `RouteEntry` se encarga de esto.

Una `RouteEntry` sitúa una estación en una posición absoluta de la ruta (en metros desde el origen). La zona de parada se define como un margen de ± `stopTolerance` metros alrededor de la posición de la estación:

```
        |<--- stopTolerance --->|<--- stopTolerance --->|
        |                       |                       |
  ------●=======================●=======================●------
   position - stopTolerance   position          position + stopTolerance
        (límite inferior)    (stopPoint)         (límite superior)
```

El código del método es el siguiente:

```kotlin
data class RouteEntry(
    val station: Station,
    val position: Int,             // posición absoluta en la ruta (metros)
    val segmentToNext: TrackSegment? = null
) {
    fun isInStopZone(trainPosition: Double): Boolean =
        kotlin.math.abs(trainPosition - position) <= station.stopTolerance
}
```

Es decir, el tren está en la zona de parada si la distancia entre su posición y la posición de la estación es **menor o igual** que `stopTolerance`.

### Datos del escenario

Para este ejercicio, considerad una estación con los siguientes valores:

- **Estación**: "Crevillente"
- **`position`**: 44700 m (posición absoluta en la ruta)
- **`stopTolerance`**: 50 m (valor por defecto)

Por tanto, la zona de parada válida abarca el intervalo **[44650.0, 44750.0]**.

### Se pide

#### Apartado A — Identificación de clases de equivalencia y valores límite

1. Identificad las **clases de equivalencia** para el parámetro `trainPosition` respecto a este método. ¿Cuántas hay? ¿Cuáles son válidas (retorna `true`) y cuáles no válidas (retorna `false`)?

2. Aplicad **BVA** y listad los **6 valores límite** para el intervalo [44650.0, 44750.0]. Usad un incremento de 1.0 para los valores adyacentes.

> **Pista:** hay tres clases de equivalencia (una válida y dos no válidas) y seis valores de frontera (tres en cada límite del intervalo).

#### Apartado B — Diseño de casos de prueba

Utilizando los valores identificados en el apartado A, completad la siguiente tabla de casos de prueba. Debéis cubrir **los 6 valores límite** y al menos **un valor representativo interior** de la zona válida:

| ID | Descripción | Precondiciones | Entrada (`trainPosition`) | Resultado esperado | Resultado obtenido | Pass/Fail |
|----|-------------|----------------|---------------------------|--------------------|--------------------|-----------|
| CP-01 | | Estación "Crevillente" en posición 44700, stopTolerance = 50 | | | | |
| CP-02 | | " | | | | |
| CP-03 | | " | | | | |
| CP-04 | | " | | | | |
| CP-05 | | " | | | | |
| CP-06 | | " | | | | |
| CP-07 | | " | | | | |

> **Nota:** Las columnas "Resultado obtenido" y "Pass/Fail" se dejan vacías — se rellenan durante la ejecución de las pruebas. En un entorno automatizado (JUnit), el framework las rellena automáticamente.

## Criterios de evaluación

| Criterio | Peso |
|----------|------|
| Identificación correcta de clases de equivalencia (número, rangos, tipo) | 33%  |
| Selección correcta de valores límite | 33%  |
| Completitud y corrección de los casos de prueba | 34%  |
