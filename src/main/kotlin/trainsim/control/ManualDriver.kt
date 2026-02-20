package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.physics.TrackConditions

class ManualDriver(private val command: DriveCommand) : Driver {
    override fun drive(train: Train, conditions: TrackConditions) = command
}