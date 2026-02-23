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

#### H7 — Condiciones de vía: entorno estático inicial

La primera implementación usa un `StaticEnvironment` que devuelve condiciones uniformes en toda la ruta (vía plana, sin curvas).

**Alternativa:** `DynamicEnvironment` con tramos de pendiente, restricciones temporales de velocidad y radios de curva variables.

**Rationale:** El entorno estático permite validar el motor de física y las estrategias de conducción de forma aislada antes de añadir variabilidad de vía.

---

## TODO / Mejoras pendientes

### Modelo de dominio

- **Refactor: ¿`TrainRepository` como factory?** — evaluar si `TrainRepository` debería exponer
  métodos factory (`createKirbyPaulTank(startPosition): Train`) o si conviene separar `TrainSpec`
  (parámetros físicos inmutables) de `Train` (estado mutable), dejando el repositorio como catálogo
  de specs y delegando la construcción a un factory. La respuesta depende de si `Train` acaba
  separando spec y estado como entidades distintas.

- **Repositorio de rutas** — análogo a `TrainRepository`, un `RouteRepository` con rutas predefinidas
  y sus estaciones. Nótese que `Station` es una agregación dentro de `Route`: las distancias
  (`approachPoint`, `stopPoint`, `departurePoint`) son posiciones en la ruta, no coordenadas
  geográficas absolutas.

- **Especialización de estaciones terminales** — para la estación inicial de una ruta no tiene
  sentido `approachPoint`, ni `departurePoint` para la última. Convendría especializar `Station`
  en `TerminalStation` y `IntermediateStation`, o modelarlo con propiedades opcionales.

### Observadores y presentación

- **Velocidad en km/h en el logger** — `ConsoleLogger` muestra la velocidad en m/s. Usar
  `train.velocityKmH()` para que la salida sea legible operacionalmente.

- **Visualización ASCII de la ruta** — pintar en una línea la posición del tren y las estaciones,
  estilo barra de progreso:
  ```
  [Madrid]----·-----------[Guadalajara]
  ```

### Simulación en tiempo real

- **Control de velocidad de simulación** — la simulación actualmente escupe todos los pasos
  instantáneamente. Añadir un multiplicador de tiempo (`realTimeFactor`) usando corutinas o
  `Thread.sleep` para poder simular a x1, x10, x100, etc.

### Conducción interactiva

- **Terminal interactiva** — permitir al conductor intervenir en tiempo real desde teclado:
  - `W` / `S` → aumentar / reducir throttle
  - `B` → freno de emergencia
  - `A` → toggle autopiloto / manual
  - Requiere lectura de input no bloqueante (corutinas o hilo separado).

---

## Estructura del proyecto

```
model/        → entidades del dominio (Train, Station, Route, RouteEntry)
physics/      → motor de física (PhysicsEngine, Environment, Davis, ...)
control/      → lógica de conducción (DriveCommand, Driver, Autopilot)
simulation/   → bucle de simulación y configuración
observer/     → logging, CSV export, comprobación de llegadas
scenario/     → definición de rutas y escenarios de prueba
```
