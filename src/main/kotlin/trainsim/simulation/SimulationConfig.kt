package io.github.scontreraslopez.trainsim.simulation

data class SimulationConfig(
    val timeStep: Double = 0.1,
    val maxTime: Double = 86400.0 // 24 horas
) {
    init{
        require(timeStep > 0.0) { "El tiempo debe ser positivo" }
    }
}