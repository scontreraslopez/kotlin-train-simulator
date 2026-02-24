package io.github.scontreraslopez.trainsim.model

/**
 * Vincula una Station con su posición absoluta en una ruta concreta,
 * más el tramo de vía que sale de ella hacia la siguiente estación.
 *
 * [position] es la posición del stopPoint de la estación en metros desde
 * el origen de la ruta. [segmentToNext] describe el track hasta la siguiente
 * estación; en la estación terminal se deja el valor por defecto (FLAT).
 */
data class RouteEntry(
    val station: Station,
    val position: Int,
    val segmentToNext: TrackSegment? = null  // null en la estación terminal
) {
    val approachPoint: Int get() = position - station.approachDistance
    val departurePoint: Int get() = position + station.departureDistance

    fun isInApproachZone(trainPosition: Double) =
        station.approachDistance > 0 && trainPosition in approachPoint.toDouble()..position.toDouble()

    fun isInDepartureZone(trainPosition: Double) =
        station.departureDistance > 0 && trainPosition in position.toDouble()..departurePoint.toDouble()

    fun hasReachedStop(trainPosition: Double) = trainPosition >= position
}
