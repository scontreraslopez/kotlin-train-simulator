package io.github.scontreraslopez.trainsim

import io.github.scontreraslopez.trainsim.control.DriveCommand
import io.github.scontreraslopez.trainsim.control.ManualDriver
import io.github.scontreraslopez.trainsim.data.TrainRepository
import io.github.scontreraslopez.trainsim.model.Route
import io.github.scontreraslopez.trainsim.model.RouteEntry
import io.github.scontreraslopez.trainsim.model.Station
import io.github.scontreraslopez.trainsim.model.TrackSegment
import io.github.scontreraslopez.trainsim.observer.ConsoleLogger
import io.github.scontreraslopez.trainsim.scenario.SimpleRouteScenario
import io.github.scontreraslopez.trainsim.simulation.Simulator

fun main() {
    val madrid = Station(
        name = "Madrid Atocha",
        approachDistance = 0,
        departureDistance = 1_000,
        maxApproachSpeed = 30.0 / 3.6,
        maxDepartureSpeed = 30.0 / 3.6
    )
    val guadalajara = Station(
        name = "Guadalajara",
        approachDistance = 1_000,
        departureDistance = 0,
        maxApproachSpeed = 30.0 / 3.6,
        maxDepartureSpeed = 30.0 / 3.6
    )

    val route = Route(
        listOf(
            RouteEntry(
                station = madrid,
                position = 0,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 200.0 / 3.6)
            ),
            RouteEntry(
                station = guadalajara,
                position = 59_000
            )
        )
    )

    val scenario = SimpleRouteScenario(
        description = "Madrid → Guadalajara",
        route = route
    )

    val simulator = Simulator(
        train = TrainRepository.kirbyPaulTank(),
        driver = ManualDriver(DriveCommand.FULL_THROTTLE),
        scenario = scenario,
        observers = listOf(ConsoleLogger())
    )

    simulator.run()
}
