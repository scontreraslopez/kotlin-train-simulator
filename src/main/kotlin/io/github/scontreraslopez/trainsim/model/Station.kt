package io.github.scontreraslopez.trainsim.model

/**
 * Representa una estación como entidad independiente de cualquier ruta.
 *
 * El stopPoint es el origen de coordenadas de la estación. Las zonas de
 * aproximación y salida se expresan como distancias relativas a ese punto:
 *
 *   |<-- approachDistance -->|  stopPoint  |<-- departureDistance -->|
 *   approachPoint                    0                        departurePoint
 *
 * Para estaciones terminales (origen o destino de una ruta) se puede usar
 * approachDistance = 0 o departureDistance = 0 según corresponda.
 *
 * @property name Nombre de la estación.
 * @property approachDistance Metros antes del stop donde comienza la restricción de velocidad de entrada.
 * @property departureDistance Metros después del stop donde termina la restricción de velocidad de salida.
 * @property maxApproachSpeed Velocidad máxima en m/s en la zona de aproximación.
 * @property maxDepartureSpeed Velocidad máxima en m/s en la zona de salida.
 */
data class Station(
    val name: String,
    val approachDistance: Int,
    val departureDistance: Int,
    val maxApproachSpeed: Double,
    val maxDepartureSpeed: Double
) {
    init {
        require(approachDistance >= 0) { "approachDistance debe ser no negativa" }
        require(departureDistance >= 0) { "departureDistance debe ser no negativa" }
        require(maxApproachSpeed > 0) { "maxApproachSpeed debe ser positiva" }
        require(maxDepartureSpeed > 0) { "maxDepartureSpeed debe ser positiva" }
    }

    fun maxApproachSpeedKmH() = maxApproachSpeed * 3.6
    fun maxDepartureSpeedKmH() = maxDepartureSpeed * 3.6
    fun approachDistanceKm() = approachDistance / 1000.0
    fun departureDistanceKm() = departureDistance / 1000.0
}
