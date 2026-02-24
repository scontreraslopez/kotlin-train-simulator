package io.github.scontreraslopez.trainsim.scenario

import io.github.scontreraslopez.trainsim.model.Route
import io.github.scontreraslopez.trainsim.model.Train

class SimpleRouteScenario(
    override val description: String,
    override val route: Route
) : Scenario {
    override fun isCompleted(train: Train): Boolean {
        val lastEntry = route.routeEntries.last()
        return train.position >= lastEntry.position
    }
}
