package io.github.scontreraslopez.trainsim.simulation

import io.github.scontreraslopez.trainsim.control.Driver
import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.observer.SimulationObserver
import io.github.scontreraslopez.trainsim.physics.PhysicsEngine
import io.github.scontreraslopez.trainsim.scenario.Scenario

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
            val context = scenario.route.drivingContextAt(train.position) ?: break
            val command = driver.drive(train, context)
            PhysicsEngine.step(train, command, context.segment, config.timeStep)
            observers.forEach { it.onStep(train, command, context, simulationTime) }
            simulationTime += config.timeStep
        }
    }
}
