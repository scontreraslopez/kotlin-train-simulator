package io.github.scontreraslopez.trainsim.data

import io.github.scontreraslopez.trainsim.model.Route
import io.github.scontreraslopez.trainsim.model.RouteEntry
import io.github.scontreraslopez.trainsim.model.Station
import io.github.scontreraslopez.trainsim.model.TrackSegment
import io.github.scontreraslopez.trainsim.scenario.SimpleRouteScenario
import io.github.scontreraslopez.trainsim.scenario.Scenario

/**
 * Catálogo de escenarios predefinidos.
 *
 * Cada función devuelve un [Scenario] listo para usar en el [Simulator].
 * El tren y el driver se eligen por separado en el punto de entrada.
 */
object ScenarioRepository {

    /**
     * Madrid Atocha → Guadalajara.
     * Tramo recto de 59 km a 200 km/h, sin pendiente.
     */
    fun madridGuadalajara(): Scenario = SimpleRouteScenario(
        description = "Madrid Atocha → Guadalajara (59 km)",
        route = Route(
            listOf(
                RouteEntry(
                    station = Station(
                        name = "Madrid Atocha",
                        approachDistance = 0,
                        departureDistance = 1_000,
                        maxApproachSpeed = 30.0 / 3.6,
                        maxDepartureSpeed = 30.0 / 3.6
                    ),
                    position = 0,
                    segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 200.0 / 3.6)
                ),
                RouteEntry(
                    station = Station(
                        name = "Guadalajara",
                        approachDistance = 1_000,
                        departureDistance = 0,
                        maxApproachSpeed = 30.0 / 3.6,
                        maxDepartureSpeed = 30.0 / 3.6
                    ),
                    position = 59_000
                )
            )
        )
    )

    /**
     * Cercanías C-1: Murcia del Carmen → Alacant/Alicante Terminal.
     * Línea regional de 75 km con 11 estaciones.
     */
    fun cercaniasMurciaAlicante(): Scenario = SimpleRouteScenario(
        description = "Cercanías C-1: Murcia del Carmen → Alacant/Alicante Terminal (75 km)",
        route = RouteRepository.cercaniasMurciaAlicanteC1
    )

    fun all(): List<Scenario> = listOf(madridGuadalajara(), cercaniasMurciaAlicante())
}
