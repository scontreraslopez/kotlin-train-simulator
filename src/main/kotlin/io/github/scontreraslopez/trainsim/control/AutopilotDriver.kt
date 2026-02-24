package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.Train
import io.github.scontreraslopez.trainsim.model.TrackSegment

//TODO: Esta clase debe ser implementada.

class AutopilotDriver : Driver {

    enum class Phase {
        STOPPED,      // brake = 1, throttle = 0
        ACCELERATING, // throttle = 1, brake = 0
        CRUISING,     // throttle = Kp*error, brake = 0
        COASTING,     // throttle = 0, brake = 0
        BRAKING       // throttle = 0, brake > 0
    }

    var phase: Phase = Phase.STOPPED
        private set

    override fun drive(train: Train, segment: TrackSegment): DriveCommand {
        TODO("Not yet implemented")
    }
}
