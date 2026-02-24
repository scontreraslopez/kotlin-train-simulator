package io.github.scontreraslopez.trainsim.model

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Tests unitarios para [Route.segmentAt].
 *
 * Estrategia: Boundary Value Analysis (BVA).
 * Los bugs tienden a concentrarse en los límites de los rangos de entrada.
 * Para cada frontera relevante se definen tres casos:
 *   - justo antes del límite
 *   - exactamente en el límite
 *   - justo después del límite
 *
 * Las fronteras identificadas en [Route.segmentAt] son:
 *   F1 — posición de la primera estación (position = 0)
 *   F2 — posición de una estación intermedia (stopPoint de una estación que no es terminal)
 *   F3 — posición de la última estación (terminal, segmentToNext = null)
 */
class RouteTest {

    // -------------------------------------------------------------------------
    // Fixture compartida
    //
    // Ruta mínima con dos estaciones (A → B) suficiente para la mayoría de tests.
    // Separadas 10 000 m para que los casos "antes/en/después" sean inequívocos.
    //
    //   posición 0        posición 10 000
    //   [Estación A] ---- trackSegmentAB ---- [Estación B (terminal)]
    // -------------------------------------------------------------------------

    private val trackSegmentAB = TrackSegment(grade = 0.0, lineSpeedLimit = 100.0 / 3.6)

    private fun buildSimpleRoute() = Route(
        routeEntries = listOf(
            RouteEntry(
                station = Station(
                    name = "A",
                    approachDistance = 0,
                    departureDistance = 0,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 0,
                segmentToNext = trackSegmentAB
            ),
            RouteEntry(
                station = Station(
                    name = "B",
                    approachDistance = 0,
                    departureDistance = 0,
                    maxApproachSpeed = 50.0 / 3.6,
                    maxDepartureSpeed = 50.0 / 3.6
                ),
                position = 10_000,
                segmentToNext = null  // estación terminal: no hay tramo siguiente
            )
        )
    )

    // -------------------------------------------------------------------------
    // F3 — frontera de la estación terminal (position = 10 000)
    //
    // Caso implementado: exactamente en el límite.
    // segmentAt devuelve el segmentToNext de la última RouteEntry cuya posición
    // sea <= trainPosition. En la terminal ese valor es null.
    // -------------------------------------------------------------------------

    /**
     * BVA · F3 · en el límite.
     *
     * El tren está exactamente sobre el stopPoint de la estación terminal.
     * No hay tramo siguiente, por lo que segmentAt debe devolver null.
     * Este es el mecanismo que usa [Simulator] para detectar fin de ruta.
     */
    @Test
    fun segmentAtReturnsNullAtTerminalStation() {
        val route = buildSimpleRoute()

        val result = route.segmentAt(10_000.0)

        assertNull(result, "segmentAt should return null at the terminal station")
    }

    // -------------------------------------------------------------------------
    // TODO (BVA · F3 · antes del límite)
    // Posición justo antes de la terminal (p. ej. 9 999 m).
    // Debe devolver trackSegmentAB, no null.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // TODO (BVA · F3 · después del límite)
    // Posición más allá de la terminal (p. ej. 10 001 m — tren fuera de ruta).
    // ¿Sigue devolviendo null? ¿Lanza excepción? Documentar el comportamiento esperado.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // TODO (BVA · F2 · antes del límite)
    // Ruta de tres estaciones. Posición justo antes del stopPoint intermedio.
    // Debe devolver el segmento del primer tramo.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // TODO (BVA · F2 · en el límite)
    // Posición exactamente en el stopPoint de la estación intermedia.
    // ¿Devuelve el segmento de entrada o el de salida? Revisar la lógica de
    // lastOrNull en Route.segmentAt — las clases de equivalencia se tocan aquí.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // TODO (BVA · F2 · después del límite)
    // Posición justo después del stopPoint intermedio.
    // Debe devolver el segmento del segundo tramo.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // TODO (BVA · F1 · antes del límite)
    // Posición negativa (p. ej. -1 m — antes del origen de la ruta).
    // Comportamiento no especificado: ¿null? ¿excepción? Definir y documentar.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // TODO (BVA · F1 · en el límite)
    // Posición exactamente en el origen (position = 0).
    // Debe devolver trackSegmentAB.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // TODO (BVA · F1 · después del límite)
    // Posición justo después del origen (p. ej. 1 m).
    // Debe devolver trackSegmentAB.
    // -------------------------------------------------------------------------
}
