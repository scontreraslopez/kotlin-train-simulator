package io.github.scontreraslopez.trainsim.model

/**
 * Contexto de conducción en un instante dado de la simulación.
 *
 * Agrupa toda la información que un [io.github.scontreraslopez.trainsim.control.Driver]
 * necesita para tomar una decisión, sin acceder directamente a la [Route].
 *
 * Es un value object inmutable: se construye en cada paso por [Route.drivingContextAt]
 * y se descarta al final del paso.
 *
 * @property segment            Tramo de vía activo (pendiente, límite de velocidad de línea).
 * @property distanceToNextStop Metros hasta el stopPoint de la próxima estación.
 *                              [Double.MAX_VALUE] si no hay próxima estación (tramo final).
 * @property nextStopApproachSpeed Velocidad máxima de entrada a la próxima estación en m/s.
 * @property isInApproachZone   El tren está en la zona de aproximación de la próxima estación.
 * @property isInDepartureZone  El tren está en la zona de salida de la última estación superada.
 * @property isInStopZone       El tren está dentro del margen de parada válido de la próxima
 *                              estación (±[Station.stopTolerance] metros respecto al stopPoint).
 */
data class DrivingContext(
    val segment: TrackSegment,
    val distanceToNextStop: Double,
    val nextStopApproachSpeed: Double,
    val isInApproachZone: Boolean,
    val isInDepartureZone: Boolean,
    val isInStopZone: Boolean
)
