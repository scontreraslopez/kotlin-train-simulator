package io.github.scontreraslopez.trainsim

import io.github.scontreraslopez.trainsim.control.DriveCommand
import io.github.scontreraslopez.trainsim.control.ManualDriver
import io.github.scontreraslopez.trainsim.data.TrainRepository
import io.github.scontreraslopez.trainsim.model.Route
import io.github.scontreraslopez.trainsim.model.RouteEntry
import io.github.scontreraslopez.trainsim.model.Station
import io.github.scontreraslopez.trainsim.observer.ConsoleLogger
import io.github.scontreraslopez.trainsim.physics.StaticEnvironment
import io.github.scontreraslopez.trainsim.scenario.SimpleRouteScenario
import io.github.scontreraslopez.trainsim.simulation.Simulator

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val madrid = Station(
        name = "Madrid Atocha",
        approachPoint = 0,
        stopPoint = 500,
        departurePoint = 1000,
        maxApproachSpeed = 30.0 / 3.6,
        maxDepartureSpeed = 30.0 / 3.6
    )
    val guadalajara = Station(
        name = "Guadalajara",
        approachPoint = 58_000,
        stopPoint = 59_000,
        departurePoint = 60_000,
        maxApproachSpeed = 30.0 / 3.6,
        maxDepartureSpeed = 30.0 / 3.6
    )

    // 2. Ruta
    val route = Route(
        listOf(
            RouteEntry(madrid, 0),
            RouteEntry(guadalajara, 59_000)
        )
    )

    // 3. Escenario
    val scenario = SimpleRouteScenario(
        description = "Madrid → Guadalajara",
        route = route,
        environment = StaticEnvironment(200.0 / 3.6)
    )

    // 4. Simulación
    val simulator = Simulator(
        train = TrainRepository.kirbyPaulTank(),
        driver = ManualDriver(DriveCommand.FULL_THROTTLE),
        scenario = scenario,
        observers = listOf(ConsoleLogger())
    )

    simulator.run()
}
