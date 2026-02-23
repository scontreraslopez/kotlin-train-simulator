package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.model.TrackSegment

class ManualDriver(private val command: DriveCommand) : Driver {
    override fun drive(train: Train, segment: TrackSegment) = command
}