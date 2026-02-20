package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.physics.TrackConditions

class AutopilotDriver: Driver {
    override fun drive(
        train: Train,
        conditions: TrackConditions
    ): DriveCommand {
        TODO("Not yet implemented")
    }
}