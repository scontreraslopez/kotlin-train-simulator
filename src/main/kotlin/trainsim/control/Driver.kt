package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.model.TrackSegment

//El driver dice que hacer (DriveCommand) a partir de la situación actual del tren y las condiciones de la via
interface Driver {
    fun drive(train: Train, segment: TrackSegment): DriveCommand
}