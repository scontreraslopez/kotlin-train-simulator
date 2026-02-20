package io.github.scontreraslopez.trainsim.scenario

import io.github.scontreraslopez.trainsim.model.Route
import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.physics.Environment
import io.github.scontreraslopez.trainsim.simulation.Scenario

class SimpleRouteScenario(
    override val description: String,
    override val route: Route,
    override val environment: Environment
) : Scenario {
    override fun isCompleted(train: Train): Boolean {
        val lastStation = route.routeEntries.last().station
        return train.position >= lastStation.stopPoint
    }
}
