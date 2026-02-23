package io.github.scontreraslopez.trainsim.model

/**
 * Tramo de vía entre dos estaciones consecutivas.
 *
 * Modela las propiedades de infraestructura del track: pendiente y límite
 * de velocidad de línea. La longitud del tramo es implícita (diferencia de
 * posiciones entre los dos RouteEntry consecutivos).
 *
 * @property grade Pendiente en permil (‰). Positivo = cuesta arriba.
 * @property lineSpeedLimit Límite de velocidad de línea en m/s.
 */
data class TrackSegment(
    val grade: Double = 0.0,
    val lineSpeedLimit: Double = DEFAULT_LINE_SPEED_LIMIT
) {
    companion object {
        private const val DEFAULT_LINE_SPEED_LIMIT = 350.0 / 3.6  // 350 km/h — sin restricción efectiva
    }

    fun lineSpeedLimitKmH() = lineSpeedLimit * 3.6
}
