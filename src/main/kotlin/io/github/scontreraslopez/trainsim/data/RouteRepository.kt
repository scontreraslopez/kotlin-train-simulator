package io.github.scontreraslopez.trainsim.data

import io.github.scontreraslopez.trainsim.model.Route
import io.github.scontreraslopez.trainsim.model.RouteEntry
import io.github.scontreraslopez.trainsim.model.Station
import io.github.scontreraslopez.trainsim.model.TrackSegment

/**
 * Catálogo de rutas predefinidas de Cercanías Renfe.
 *
 * Distancias reales entre estaciones; velocidades de aproximación/salida
 * estimadas para tráfico regional (50 km/h).
 *
 * Las distancias de approach/departure son relativas al stopPoint de cada
 * estación. Las terminales usan approachDistance = 0 (origen) o
 * departureDistance = 0 (destino) según corresponda.
 */
class RouteRepository {

    /**
     * Cercanías C-1: Murcia del Carmen → Alacant/Alicante Terminal.
     * Distancia total: 75,0 km · 11 estaciones.
     *
     * Tramo                                        Distancia    Acumulada
     * Murcia del Carmen → Beniel                   16,4 km      16,4 km
     * Beniel → Orihuela Miguel Hernández            7,0 km      23,4 km
     * Orihuela Miguel Hernández → Callosa           7,0 km      30,4 km
     * Callosa → San Isidro-Albatera-Catral          5,3 km      35,7 km
     * San Isidro-Albatera-Catral → Crevillente      9,0 km      44,7 km
     * Crevillente → Elx/Elche Carrús                8,8 km      53,5 km
     * Elx/Elche Carrús → Elx/Elche Parc             1,5 km      55,0 km
     * Elx/Elche Parc → Torrellano                   9,5 km      64,5 km
     * Torrellano → Sant Gabriel                     8,7 km      73,2 km
     * Sant Gabriel → Alacant/Alicante Terminal      1,8 km      75,0 km
     */
    val cercaniasMurciaAlicanteC1: Route = Route(
        listOf(
            RouteEntry(
                station = Station(
                    name = "Murcia del Carmen",
                    approachDistance = 0,       // terminal origen
                    departureDistance = 1_000,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 0,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                station = Station(
                    name = "Beniel",
                    approachDistance = 2_000,
                    departureDistance = 1_000,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 16_400,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                station = Station(
                    name = "Orihuela Miguel Hernández",
                    approachDistance = 2_000,
                    departureDistance = 1_000,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 23_400,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                station = Station(
                    name = "Callosa de Segura",
                    approachDistance = 2_000,
                    departureDistance = 1_000,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 30_400,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                station = Station(
                    name = "San Isidro-Albatera-Catral",
                    approachDistance = 2_000,
                    departureDistance = 1_000,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 35_700,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                station = Station(
                    name = "Crevillente",
                    approachDistance = 2_000,
                    departureDistance = 1_000,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 44_700,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                // Zona de salida reducida: Parc está a solo 1,5 km
                station = Station(
                    name = "Elx/Elche Carrús",
                    approachDistance = 2_000,
                    departureDistance = 600,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 53_500,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                // Zona de aproximación reducida: Carrús está a solo 1,5 km
                station = Station(
                    name = "Elx/Elche Parc",
                    approachDistance = 700,
                    departureDistance = 1_000,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 55_000,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                station = Station(
                    name = "Torrellano",
                    approachDistance = 2_000,
                    departureDistance = 1_000,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 64_500,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                // Zona de salida reducida: Terminal está a solo 1,8 km
                station = Station(
                    name = "Sant Gabriel",
                    approachDistance = 2_000,
                    departureDistance = 700,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 73_200,
                segmentToNext = TrackSegment(grade = 0.0, lineSpeedLimit = 160.0 / 3.6)
            ),
            RouteEntry(
                station = Station(
                    name = "Alacant/Alicante Terminal",
                    approachDistance = 1_100,
                    departureDistance = 0,      // terminal destino
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 75_000
            )
        )
    )
}
