package io.github.scontreraslopez.trainsim.observer

import io.github.scontreraslopez.trainsim.control.DriveCommand
import io.github.scontreraslopez.trainsim.model.DrivingContext
import io.github.scontreraslopez.trainsim.model.Train

interface SimulationObserver {
    fun onStep(train: Train, command: DriveCommand, context: DrivingContext, time: Double)
}