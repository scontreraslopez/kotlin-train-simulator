package io.github.scontreraslopez.trainsim.simulation

import io.github.scontreraslopez.trainsim.control.Driver
import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.observer.SimulationObserver
import io.github.scontreraslopez.trainsim.physics.PhysicsEngine

class Simulator(
    val train: Train,
    val driver: Driver,
    val scenario: Scenario,
    val config: SimulationConfig = SimulationConfig(),
    val observers: List<SimulationObserver> = emptyList()
) {
    private var simulationTime: Double = 0.0

    fun run() {
        while (!scenario.isCompleted(train) && simulationTime < config.maxTime) {
            val segment = scenario.route.segmentAt(train.position) ?: break
            val command = driver.drive(train, segment)
            PhysicsEngine.step(train, command, segment, config.timeStep)
            observers.forEach { it.onStep(train, command, segment, simulationTime) }
            simulationTime += config.timeStep
        }
    }
}
