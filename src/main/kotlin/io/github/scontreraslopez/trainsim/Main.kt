package io.github.scontreraslopez.trainsim

import io.github.scontreraslopez.trainsim.data.RouteRepository
import io.github.scontreraslopez.trainsim.data.ScenarioRepository
import io.github.scontreraslopez.trainsim.data.TrainRepository
import io.github.scontreraslopez.trainsim.simulation.SimulationFactory
import io.github.scontreraslopez.trainsim.ui.ConsoleMenu

fun main() {
    val trainRepository = TrainRepository()
    val routeRepository = RouteRepository()
    val scenarioRepository = ScenarioRepository(routeRepository)
    val factory = SimulationFactory()

    val trains = trainRepository.all()
    val train = trains[ConsoleMenu.select("Elige un tren:", trains.map { it.name })]

    val scenarios = scenarioRepository.all()
    val scenario = scenarios[ConsoleMenu.select("Elige un escenario:", scenarios.map { it.description })]

    factory.create(train, scenario).run()
}
