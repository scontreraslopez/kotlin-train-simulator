package io.github.scontreraslopez.trainsim.scenario

import io.github.scontreraslopez.trainsim.model.Route
import io.github.scontreraslopez.trainsim.model.Train

interface Scenario {
    val description: String
    val route: Route
    fun isCompleted(train: Train): Boolean
}
