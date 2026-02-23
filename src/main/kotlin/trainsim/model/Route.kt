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
}
