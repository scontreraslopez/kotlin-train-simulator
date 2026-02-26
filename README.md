# Kotlin Train Simulator

Simulador de dinámica ferroviaria con física newtoniana. Orientado a validar estrategias de conducción (manual y autopiloto) sobre rutas con estaciones, restricciones de velocidad y condiciones de vía.

---

## Física del modelo

### Ecuación de movimiento

```
a = F_neta / (m · ξ)

F_neta = F_tracción - F_frenado - F_resistencia - F_rampa
```

| Símbolo | Descripción                        | Unidades |
|---------|------------------------------------|----------|
| `m`     | Masa del tren                      | kg       |
| `ξ`     | Factor de masa rotante             | adim.    |
| `a`     | Aceleración                        | m/s²     |

Todas las magnitudes se almacenan en **SI** (m, m/s, kg, N). Los getters de presentación (`velocityKmH()`, `maxApproachSpeedKmH()`, etc.) convierten a unidades operacionales.

---

### Hipótesis y simplificaciones

#### H1 — Integración numérica: Euler explícito

```
v(t+dt) = v(t) + a(t) · dt
x(t+dt) = x(t) + v(t) · dt
```

**Alternativa:** Runge-Kutta de orden 4 (RK4), que reduce el error de truncación de O(dt²) a O(dt⁴).

**Rationale:** Con pasos de simulación `dt ≤ 0.5 s` el error acumulado de Euler es despreciable para las velocidades y distancias típicas de un tren. RK4 añadiría complejidad sin beneficio observable en este contexto.

---

#### H2 — Modelo de tracción: límite por potencia y esfuerzo máximo

```
F_t = min(F_t_max,  P_max / max(v, ε))  ×  throttle
```

- A baja velocidad la tracción está limitada por la adherencia rueda-carril (`F_t_max`).
- A alta velocidad está limitada por la potencia disponible (`P_max / v`).
- `ε` es una velocidad mínima de guarda para evitar división por cero.

**Alternativa:** Curva de tracción discreta (lookup table) extraída de la hoja de características real del material rodante.

**Rationale:** La curva hiperbólica captura el comportamiento esencial (zona de par constante / zona de potencia constante) con solo dos parámetros (`P_max`, `F_t_max`), suficiente para simular estrategias de conducción sin datos reales del vehículo.

---

#### H3 — Modelo de frenado: fuerza constante máxima

```
F_b = brake × F_b_max
```

**Alternativa:** Frenado proporcional al peso normal (coeficiente de fricción rueda-carril variable con velocidad y condición del carril), o modelo neumático con dinámica propia de la tubería de freno.

**Rationale:** El objetivo del simulador es evaluar *cuándo* frenar, no modelar la dinámica interna del sistema de frenos. Un `F_b_max` calibrado empíricamente es suficiente para reproducir las distancias de frenado operacionales.

---

#### H4 — Resistencia al avance: ecuación de Davis

```
F_r = A + B·v + C·v²
```

| Término | Origen físico                              |
|---------|--------------------------------------------|
| `A`     | Resistencia de rodadura (cojinetes, carril)|
| `B·v`   | Rozamiento de pestaña, resistencia de vía  |
| `C·v²`  | Arrastre aerodinámico                      |

**Alternativa:** Separar explícitamente resistencia aerodinámica (`½·ρ·Cd·A·v²`) de la resistencia mecánica, con coeficientes obtenidos de ensayos en túnel de viento.

**Rationale:** Davis es el estándar industrial ferroviario desde hace más de un siglo y sus coeficientes están tabulados para la mayoría de material rodante. La separación explícita solo tiene sentido cuando se dispone de datos de geometría exterior del vehículo.

---

#### H5 — Resistencia de rampa: pendiente pequeña

```
F_g = m · g · sin(θ)  ≈  m · g · (grade‰ / 1000)
```

**Alternativa:** Usar `sin(θ)` exacto, relevante para pendientes superiores al 5 % (50 ‰).

**Rationale:** Las rampas ferroviarias convencionales raramente superan 25-35 ‰ (metro urbano hasta ~60 ‰). En ese rango `sin(θ) ≈ tan(θ) = grade‰/1000` con error < 0.1 %.

---

#### H6 — Factor de masa rotante: constante

```
ξ = 1.06  (valor típico para tren de viajeros)
```

**Alternativa:** Calcular `ξ` dinámicamente a partir de los momentos de inercia de ruedas, ejes y motores de tracción.

**Rationale:** `ξ` varía poco en operación normal. La incertidumbre en la masa del tren (pasajeros, carga) domina sobre la variación de `ξ`.

---

#### H7 — Condiciones de vía: `TrackSegment` estático por tramo

Cada par de estaciones consecutivas tiene un único `TrackSegment` con `grade` (‰) y `lineSpeedLimit` constantes para todo el tramo. Las condiciones de vía se resuelven una vez por paso mediante `Route.drivingContextAt(position)`.

**Alternativa:** `TrackSegment` dinámico con pendiente y límite de velocidad variables por posición dentro del tramo (p. ej. perfil altimétrico real).

**Rationale:** Un segmento uniforme por tramo permite modelar rutas reales con datos mínimos y valida el motor de física y las estrategias de conducción de forma aislada antes de añadir variabilidad intra-tramo.

---

## TODO / Mejoras pendientes

### Control

- **`AutopilotDriver`** — máquina de estados diseñada en `Autopilot.md` (fases: STOPPED / ACCELERATING / CRUISING / BRAKING / COASTING). Es el siguiente TODO prioritario; el `DrivingContext` ya le proporciona toda la información necesaria.

### Observadores y exportación

- **`ArrivalChecker`** — observer que detecta paradas en andén usando `RouteEntry.isInStopZone()` y calcula desviaciones respecto al horario.
- **`CsvExporter`** — observer que vuelca cada paso a CSV para análisis externo.
- **Velocidad en km/h en el logger** — `ConsoleLogger` muestra la velocidad en m/s; usar `train.velocityKmH()`.

### Modelo de dominio

- **Especialización de estaciones terminales** — la estación origen no tiene `approachPoint` y la destino no tiene `departurePoint`. Modelar con `approachDistance = 0` / `departureDistance = 0` ya funciona, pero podría hacerse explícito con subtipos.
- **`TrackSegment` dinámico** — pendiente y límite de velocidad variables por posición dentro del tramo.
- **Separar `TrainSpec` de `Train`** — `TrainSpec` como parámetros físicos inmutables (catálogo), `Train` como estado mutable de simulación.

### Escenarios

- **`HeavyLoadScenario`** y **`ScenarioFactory`** — pendientes de implementar.

### UX y tiempo real

- **Visualización ASCII** — barra de progreso con posición del tren y estaciones: `[Madrid]----·---[Guadalajara]`
- **`realTimeFactor`** — multiplicador de velocidad de simulación (×1, ×10, ×100) con corutinas o `Thread.sleep`.
- **Terminal interactiva** — input no bloqueante: `W`/`S` throttle, `B` freno de emergencia, `A` toggle autopiloto/manual.

---

## Estructura del proyecto

```
model/        → entidades del dominio: Train, Station, Route, RouteEntry,
                TrackSegment, DrivingContext
physics/      → PhysicsEngine (object): Davis, tracción hiperbólica, Euler semi-implícito
control/      → Driver (interface), DriveCommand, ManualDriver, AutopilotDriver
simulation/   → Simulator (bucle principal), SimulationConfig, SimulationFactory
observer/     → SimulationObserver (interface), ConsoleLogger
scenario/     → Scenario (interface), SimpleRouteScenario
data/         → TrainRepository, RouteRepository, ScenarioRepository  ← Composition Root
ui/           → ConsoleMenu (object)
Main.kt       → Composition Root: instancia repos, cablea dependencias y arranca
```
