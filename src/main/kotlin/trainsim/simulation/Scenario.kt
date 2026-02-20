package io.github.scontreraslopez.trainsim.simulation

import io.github.scontreraslopez.trainsim.model.Route
import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.physics.Environment

interface Scenario {
    val description: String
    val route: Route
    val environment: Environment
    fun isCompleted(train: Train): Boolean
}
