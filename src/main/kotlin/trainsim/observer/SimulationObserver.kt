package io.github.scontreraslopez.trainsim.observer

import io.github.scontreraslopez.trainsim.control.DriveCommand
import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.physics.TrackConditions

interface SimulationObserver {
    fun onStep(train: Train, command: DriveCommand, conditions: TrackConditions, time: Double)
}