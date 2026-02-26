package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.DrivingContext
import io.github.scontreraslopez.trainsim.model.Train

class ManualDriver(private val command: DriveCommand) : Driver {
    override fun drive(train: Train, context: DrivingContext) = command
}