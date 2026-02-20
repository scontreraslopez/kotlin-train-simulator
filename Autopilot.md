# Autopilot — Diseño de la máquina de estados

Descripción de las transiciones de `AutopilotDriver.Phase` y la lógica de conducción asociada a cada estado.

---

## Diagrama de transiciones

```
                    ┌─────────────────────────────────────────────────────┐
                    │                                                     │
              ┌─────▼──────┐                                             │
         ┌───►│  STOPPED   │                                             │
         │    └─────┬──────┘                                             │
         │          │ salida autorizada                                  │
         │          ▼                                                     │
         │    ┌─────────────┐   v >= maxDepartureSpeed                  │
         │    │ ACCELERATING├──────────────────────────────────┐        │
         │    └─────┬───────┘   (aún en zona de estación)      │        │
         │          │                                           │        │
         │          │ v >= min(speedLimit, train.maxSpeed)      │        │
         │          │ (en vía abierta)                          ▼        │
         │          │                                    ┌──────────┐   │
         │          └───────────────────────────────────►│ CRUISING │   │
         │                                               └────┬─────┘   │
         │                                                    │         │
         │                              d <= brakeDistance    │         │
         │                              (TBD, ~5 km al stop)  │         │
         │                                                    ▼         │
         │                                             ┌──────────┐     │
         │                                             │  BRAKING │     │
         │                                             └────┬─────┘     │
         │                                                  │           │
         │                          v <= maxApproachSpeed   │           │
         │                                                  ▼           │
         │                                           ┌──────────┐       │
         │                                           │ CRUISING │       │
         │                                           └────┬─────┘       │
         │                                               │               │
         │                          d <= 100 m al stop   │               │
         │                                               ▼               │
         │                                        ┌──────────┐          │
         │                                        │  BRAKING │          │
         │                                        └────┬─────┘          │
         │                                             │                 │
         │                              v ≈ 0 AND      │                 │
         └─────────────────────────────position ≈ stop─┘                │
                                                                         │
         (fin de ruta: última estación) ─────────────────────────────────┘
```

---

## Estados y acciones

### STOPPED
**Condición de entrada:** `v ≈ 0` y `position` dentro de `±50 m` del `stopPoint`.

**Acción:** `DriveCommand(throttle=0.0, brake=1.0)` — freno mantenido para no derivar.

**Transición de salida:** salida autorizada (por ahora inmediata; en el futuro podría modelar tiempo de parada en andén).

---

### ACCELERATING
**Condición de entrada:** saliendo de `STOPPED` o de `CRUISING` en zona de estación.

**Acción:** `DriveCommand(throttle=1.0, brake=0.0)` — plena potencia.

**Transiciones de salida:**
- Si el tren **aún está en zona de estación** (`position < departurePoint`): cuando `v >= maxDepartureSpeed` → `CRUISING`
- Si el tren **está en vía abierta**: cuando `v >= min(conditions.speedLimit, train.maxSpeed)` → `CRUISING`

---

### CRUISING
**Condición de entrada:** velocidad objetivo alcanzada.

**Acción:** controlador proporcional para mantener velocidad:
```
throttle = ((targetSpeed - v) * Kp).coerceIn(0.0, 1.0)
brake    = 0.0
```
`Kp` por determinar empíricamente. `targetSpeed` depende de la zona:
- En zona de estación: `maxDepartureSpeed` o `maxApproachSpeed`
- En vía abierta: `min(conditions.speedLimit, train.maxSpeed)`

**Transiciones de salida:**
- `d <= brakeDistance` a la próxima estación → `BRAKING`  *(brakeDistance TBD, estimado ~5 km)*

---

### COASTING
**Condición de entrada:** *(reservado para uso futuro)*.

**Acción:** `DriveCommand.COAST` — sin tracción ni freno, el tren decelera por inercia (Davis + rampa).

**Nota:** puede insertarse entre `CRUISING` y `BRAKING` como fase de transición suave antes de aplicar freno. No implementado en la primera versión del autopiloto.

---

### BRAKING
El autopiloto usa dos subfases de frenado (mismo estado, distinto `brake`):

#### Frenado de aproximación (lejos de la estación)
**Condición de entrada:** `d <= brakeDistance` (~5 km al `stopPoint`).

**Acción:** frenado moderado al 50 %:
```
DriveCommand(throttle=0.0, brake=0.5)
```

**Transición de salida:** cuando `v <= maxApproachSpeed` → `CRUISING` a velocidad de aproximación.

#### Frenado de parada (últimos 100 m)
**Condición de entrada:** `d <= 100 m` al `stopPoint`.

**Acción:** frenado proporcional a la distancia restante:
```
brake = ((v - targetStopSpeed) / train.maxSpeed).coerceIn(0.0, 1.0)
```
donde `targetStopSpeed` decrece linealmente a cero conforme se acerca al `stopPoint`.

**Transición de salida:** `v ≈ 0` y `|position - stopPoint| <= 50 m` → `STOPPED`.

---

## Parámetros por determinar (TBD)

| Parámetro | Descripción | Valor inicial sugerido |
|---|---|---|
| `brakeDistance` | Distancia al stop donde comienza el frenado de aproximación | 5 000 m |
| `stopZone` | Margen de posición aceptable para considerar parada válida | ±50 m |
| `Kp` | Ganancia del controlador proporcional en `CRUISING` | A calibrar en simulación |

---

## Pendiente

- Modelar **tiempo de parada en andén** antes de transicionar de `STOPPED` a `ACCELERATING`.
- Decidir si `COASTING` se activa como fase previa al frenado de aproximación.
- Calcular `brakeDistance` dinámicamente a partir de la física: `d = v² / (2 · a_max)` donde `a_max = maxBrakingForce / (mass · ξ)`.
