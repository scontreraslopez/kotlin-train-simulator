package io.github.scontreraslopez.trainsim

import io.github.scontreraslopez.trainsim.control.DriveCommand
import io.github.scontreraslopez.trainsim.control.ManualDriver
import io.github.scontreraslopez.trainsim.data.ScenarioRepository
import io.github.scontreraslopez.trainsim.data.TrainRepository
import io.github.scontreraslopez.trainsim.observer.ConsoleLogger
import io.github.scontreraslopez.trainsim.simulation.Simulator

fun main() {

    val simulator = Simulator(
        train = TrainRepository.kirbyPaulTank(),
        driver = ManualDriver(DriveCommand.FULL_THROTTLE),
        scenario = ScenarioRepository.madridGuadalajara(),
        observers = listOf(ConsoleLogger())
    )

    simulator.run()
}
