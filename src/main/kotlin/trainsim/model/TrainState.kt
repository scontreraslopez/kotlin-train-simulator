package io.github.scontreraslopez.trainsim.model

enum class TrainState {
    STOPPED, // brake = 1, throttle = 0
    ACCELERATING, // throttle = 1, brake = 0
    CRUISING,  // throttle = Kp*error, brake = 0
    COASTING, // throttle=0, brake=0
    BRAKING // throttle=0, brake>0
}