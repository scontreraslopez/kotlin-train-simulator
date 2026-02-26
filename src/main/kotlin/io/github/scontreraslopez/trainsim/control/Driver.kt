package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.DrivingContext
import io.github.scontreraslopez.trainsim.model.Train

// El driver decide qué hacer (DriveCommand) a partir del estado del tren y el contexto de conducción.
interface Driver {
    fun drive(train: Train, context: DrivingContext): DriveCommand
}