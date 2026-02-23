package io.github.scontreraslopez.trainsim.simulation

import io.github.scontreraslopez.trainsim.control.DriveCommand
import io.github.scontreraslopez.trainsim.control.Driver
import io.github.scontreraslopez.trainsim.control.ManualDriver
import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.observer.ConsoleLogger
import io.github.scontreraslopez.trainsim.observer.SimulationObserver
import io.github.scontreraslopez.trainsim.scenario.Scenario

/**
 * Ensambla un [Simulator] a partir de las elecciones del usuario.
 *
 * Centraliza los defaults de driver, configuración y observers, de forma
 * que el punto de entrada no necesite conocer esos detalles.
 */
class SimulationFactory(
    private val config: SimulationConfig = SimulationConfig(),
    private val observers: List<SimulationObserver> = listOf(ConsoleLogger())
) {
    fun create(
        train: Train,
        scenario: Scenario,
        driver: Driver = ManualDriver(DriveCommand.FULL_THROTTLE)
    ): Simulator = Simulator(
        train = train,
        driver = driver,
        scenario = scenario,
        config = config,
        observers = observers
    )
}
