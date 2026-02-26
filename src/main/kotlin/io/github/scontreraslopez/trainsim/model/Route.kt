package io.github.scontreraslopez.trainsim.model

data class Route(
    val routeEntries: List<RouteEntry>
) {
    /**
     * Devuelve el tramo de vía activo para la posición dada.
     * Corresponde al segmentToNext del último RouteEntry cuya posición sea <= trainPosition.
     * Devuelve null si el tren ha llegado a la estación terminal.
     */
    fun segmentAt(position: Double): TrackSegment? =
        routeEntries
            .lastOrNull { it.position <= position.toInt() }
            ?.segmentToNext

    /**
     * Construye el [DrivingContext] para la posición dada.
     *
     * - [DrivingContext.isInDepartureZone]: zona de salida de la última estación superada.
     * - [DrivingContext.isInApproachZone]:  zona de aproximación de la próxima estación.
     * - [DrivingContext.isInStopZone]:      margen de parada válido (±stopTolerance) de la próxima estación.
     *
     * Devuelve null cuando el tren ha llegado a la estación terminal (no hay segmento siguiente).
     */
    fun drivingContextAt(position: Double): DrivingContext? {
        val currentIndex = routeEntries.indexOfLast { it.position <= position.toInt() }
        if (currentIndex < 0) return null

        val currentEntry = routeEntries[currentIndex]
        val segment = currentEntry.segmentToNext ?: return null

        val nextEntry = routeEntries.getOrNull(currentIndex + 1)

        return DrivingContext(
            segment = segment,
            distanceToNextStop = nextEntry?.let { it.position - position } ?: Double.MAX_VALUE,
            nextStopApproachSpeed = nextEntry?.station?.maxApproachSpeed ?: segment.lineSpeedLimit,
            isInDepartureZone = currentEntry.isInDepartureZone(position),
            isInApproachZone = nextEntry?.isInApproachZone(position) ?: false,
            isInStopZone = nextEntry?.isInStopZone(position) ?: false
        )
    }
}
