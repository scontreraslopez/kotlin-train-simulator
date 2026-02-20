package io.github.scontreraslopez.trainsim.physics

class StaticEnvironment (
    val speedLimit: Double = 350.0 / 3.6 // Velocidad máxima en m/s (350 km/h convertidos a m/s). Guiño a AVE 350
): Environment {
    override fun conditionsAt(position: Double): TrackConditions {
        return TrackConditions(
            grade = 0.0, // Sin pendiente en un entorno estático
            speedLimit = speedLimit
        )
    }

}