package io.github.scontreraslopez.trainsim.observer

/**
 * TODO: Implementar como SimulationObserver.
 *
 * En cada paso de simulación comprueba si el tren ha llegado a una estación
 * dentro de la tolerancia de parada (±stopTolerance metros del stopPoint).
 *
 * Responsabilidades previstas:
 * - Detectar paradas válidas usando RouteEntry.isInStopZone() o similar.
 * - Loguear la desviación respecto al stopPoint (p. ej. "+12 m").
 * - En el futuro: comparar tiempo de llegada contra horario teórico.
 */
class ArrivalChecker {
}
