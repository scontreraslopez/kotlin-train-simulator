package io.github.scontreraslopez.trainsim.observer

/**
 * TODO: Implementar como SimulationObserver.
 *
 * En cada paso de simulación vuelca el estado completo a un fichero CSV
 * para análisis posterior en Excel, Python, etc.
 *
 * Columnas previstas:
 *   time, position, velocity, throttle, brake, grade, netForce, ...
 *
 * El fichero de salida se abriría en el constructor y se cerraría al
 * finalizar la simulación (p. ej. con Closeable o un método flush()).
 */
class CsvExporter {
}
