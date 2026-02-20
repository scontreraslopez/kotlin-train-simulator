package io.github.scontreraslopez.trainsim.model

/**
 * Representa una estación en la ruta con sus restricciones de velocidad y puntos de control.
 *
 * Una estación define tres zonas relevantes para la conducción:
 * - Zona de aproximación: desde [approachPoint] hasta [stopPoint], velocidad limitada a [maxApproachSpeed]
 * - Punto de parada: [stopPoint], donde el tren debe detenerse completamente
 * - Zona de salida: desde [stopPoint] hasta [departurePoint], velocidad limitada a [maxDepartureSpeed]
 *
 * A partir de [departurePoint] el tren puede acelerar libremente hasta la velocidad de línea.
 *
 * @property name Nombre de la estación.
 * @property approachPoint Posición en metros donde comienza la restricción de velocidad de entrada.
 * @property stopPoint Posición en metros donde el tren debe detenerse (andén).
 * @property departurePoint Posición en metros donde termina la restricción de velocidad de salida.
 * @property maxApproachSpeed Velocidad máxima en m/s en la zona de aproximación.
 * @property maxDepartureSpeed Velocidad máxima en m/s en la zona de salida.
 */
data class Station(
    val name: String,
    val approachPoint: Int,
    val stopPoint: Int,
    val departurePoint: Int,
    val maxApproachSpeed: Double,
    val maxDepartureSpeed: Double
) {
    init {
        require(approachPoint < stopPoint) {
            "approachPoint debe ser anterior a stopPoint"
        }
        require(stopPoint < departurePoint) {
            "stopPoint debe ser anterior a departurePoint"
        }
        require(maxApproachSpeed > 0) {
            "maxApproachSpeed debe ser positiva"
        }
        require(maxDepartureSpeed > 0) {
            "maxDepartureSpeed debe ser positiva"
        }
    }

    /** Comprueba si el tren está en zona de aproximación */
    fun isInApproachZone(position: Int) = position in approachPoint..stopPoint

    /** Comprueba si el tren está en zona de salida */
    fun isInDepartureZone(position: Int) = position in stopPoint..departurePoint

    /** Comprueba si el tren ha llegado al punto de parada */
    fun hasReachedStop(position: Int) = position >= stopPoint

    fun maxApproachSpeedKmH() = maxApproachSpeed * 3.6
    fun maxDepartureSpeedKmH() = maxDepartureSpeed * 3.6
    fun approachPointKm() = approachPoint / 1000.0
    fun stopPointKm() = stopPoint / 1000.0
    fun departurePointKm() = departurePoint / 1000.0

}