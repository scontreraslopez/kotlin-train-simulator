package io.github.scontreraslopez.trainsim.physics

data class TrackConditions (
    val grade: Double, // Pendiente del tramo de vía en permil (‰). Positivo = cuesta arriba.
    val speedLimit: Double // Límite de velocidad en m/s
)

interface Environment {
    fun conditionsAt(position: Double): TrackConditions
}