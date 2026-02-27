package io.github.scontreraslopez.trainsim.scenario

/**
 * TODO: Implementar como object (sin estado).
 *
 * Fábrica de escenarios predefinidos listos para ejecutar. Combina
 * RouteRepository + TrainRepository + SimulationConfig para que
 * Main.kt quede limpio:
 *
 *   val simulator = Simulator(ScenarioFactory.murciaAlicante())
 *
 * Métodos previstos:
 * - murciaAlicante(): Scenario  — C-1 con el Renfe 592 Camello
 * - madridBarcelona(): Scenario — AVE con el S-102 Talgo 350
 */
object ScenarioFactory {
}
