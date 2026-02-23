package io.github.scontreraslopez.trainsim.observer

import io.github.scontreraslopez.trainsim.control.DriveCommand
import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.model.TrackSegment

class ConsoleLogger : SimulationObserver {
    override fun onStep(train: Train, command: DriveCommand, segment: TrackSegment, time: Double) {
        println("t=%.1fs | %s".format(time, train))
    }
}