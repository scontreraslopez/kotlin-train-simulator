package io.github.scontreraslopez.trainsim.control

data class DriveCommand(
    val throttle: Double, // 0.0 to 1.0
    val brake: Double    // 0.0 to 1.0
) {
    init {
        require(throttle in 0.0..1.0) { "Throttle debe estar entre 0.0 y 1.0" }
        require(brake in 0.0..1.0) { "Freno debe estar entre 0.0 y 1.0" }
    }

    companion object {
        val COAST = DriveCommand(0.0, 0.0)
        val FULL_THROTTLE = DriveCommand(1.0, 0.0)
        val FULL_BRAKE = DriveCommand(0.0, 1.0)
    }
}

