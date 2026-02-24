# Propuesta para el repositorio "kotlin-train-simulator"

Este fichero recoge ideas y pasos prácticos para avanzar el proyecto cuando te falten ideas. Está organizado por prioridades: tareas inmediatas, mejoras estructurales y añadidos opcionales.

## Resumen rápido
- Mantener la estructura modular actual (model, control, simulation, data, ui).
- Priorizar documentación, pruebas automáticas y CI para facilitar contribuciones.
- Mejorar la testabilidad separando IO de la lógica y añadiendo observers/exports.

## Tareas inmediatas (prioridad alta)
1. Añadir README.md con:
   - Objetivo del proyecto.
   - Cómo compilar y ejecutar (usar gradle wrapper): `./gradlew run` y `./gradlew build`.
   - Ejemplo de ejecución y salida esperada.
2. Añadir LICENSE (por ejemplo MIT o Apache-2.0).
3. Añadir GitHub Actions para build + test (workflow que ejecute `./gradlew build` y `./gradlew test`).
4. Escribir tests unitarios básicos:
   - Route.segmentAt: casos frontera (posición en stopPoint, última estación).
   - SimulationConfig: validación de timeStep.
   - PhysicsEngine: si está presente, pruebas de consistencia básica.
5. Extraer IO de ConsoleMenu: crear interfaces `InputProvider` y `OutputHandler` e inyectarlas.

## Mejora de diseño y testabilidad (prioridad media)
- Inyección de dependencias simple por constructores (SimulationFactory, repositorios).
- Implementar data classes inmutables para snapshots (p. ej. TrainSnapshot) que reciban observers.
- Validaciones en constructores (masa > 0, maxPower >= 0, etc.).
- Reemplazar println/readln por logging y providers inyectables para facilitar testing.

## Motor físico y estabilidad numérica (prioridad media-alta)
- Revisar el integrador numérico actual. Considerar:
  - Semi-implicit Euler o RK4 para mayor estabilidad.
  - Tests de convergencia: comparar resultados con pasos `timeStep` y `timeStep/2`.
- Añadir límites/clamps (maxSpeed, esfuerzo de adherencia, velocidad mínima no negativa).
- Documentar las ecuaciones y supuestos (Davis, rotating mass, etc.).

## Observabilidad y exportación (prioridad media)
- Implementar SimulationObserver concretos:
  - Logger (CSV/JSON) para pasos de simulación.
  - ArrivalChecker que emita evento al llegar a estaciones y calcule retrasos.
- Añadir opción para exportar resultados a CSV/JSON para análisis externo.

## Calidad de código y automatización (prioridad media)
- Añadir ktlint y detekt al build para estilo y análisis estático.
- Añadir tareas gradle para formateo automático (si procede).

## Empaquetado y UX (prioridad baja-moderada)
- Configurar plugin `application` para facilitar `./gradlew run` y jar ejecutable.
- Añadir argumentos CLI para ejecutar en modo batch y seleccionar tren/escenario sin interacción.
- Añadir ejemplos en `/examples` y configuraciones en `/config`.

## Ideas futuras / opcionales
- Añadir controladores automáticos (PID, perfilador de velocidad) y comparativas entre ellos.
- Simular condiciones variables (pendiente, viento, wear) y comparar consumo/tiempos.
- Visualización en web o GUI (simple) para ver la simulación en tiempo real.
- Benchmarks y tests de rendimiento para simulaciones largas.

## Pasos concretos sugeridos para el próximo commit
1. Crear README.md básico + LICENSE.  
2. Añadir GitHub Actions `ci.yml` que ejecute `./gradlew build` y `./gradlew test`.  
3. Refactorizar ConsoleMenu para aceptar `InputProvider`/`OutputHandler`.  
4. Añadir un test para Route.segmentAt y configurar JUnit en gradle si no está.

---

Si quieres, puedo crear también los archivos de ejemplo (README, workflow CI, ejemplo de test y refactor de ConsoleMenu). Dime cuáles quieres que añada a continuación.