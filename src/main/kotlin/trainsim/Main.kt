package io.github.scontreraslopez.trainsim

import io.github.scontreraslopez.trainsim.data.ScenarioRepository
import io.github.scontreraslopez.trainsim.data.TrainRepository
import io.github.scontreraslopez.trainsim.simulation.SimulationFactory
import io.github.scontreraslopez.trainsim.ui.ConsoleMenu

fun main() {
    val menu = ConsoleMenu()
    val factory = SimulationFactory()

    val trains = TrainRepository.all()
    val train = trains[menu.select("Elige un tren:", trains.map { it.name })]

    val scenarios = ScenarioRepository.all()
    val scenario = scenarios[menu.select("Elige un escenario:", scenarios.map { it.description })]

    factory.create(train, scenario).run()
}
