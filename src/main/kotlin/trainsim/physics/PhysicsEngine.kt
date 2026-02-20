package io.github.scontreraslopez.trainsim.physics

import io.github.scontreraslopez.trainsim.control.DriveCommand
import io.github.scontreraslopez.trainsim.model.Train

//Singleton sin estado
object PhysicsEngine {

    private const val G = 9.81 // m/s²
    private const val EPSILON = 0.1 // Para evitar división por cero en la fórmula de tracción

    fun step(train: Train, command: DriveCommand, conditions: TrackConditions, dt: Double) {
        val netForce = computeNetForce(train, command, conditions)
        integrate(train, netForce, dt)
    }

    // Esto es lo que frena subir la pendiente, transformación de energía cinética a potencial, y lo que ayuda a bajar la pendiente, transformación de energía potencial a cinética
    private fun computeGradeForce(train: Train, grade: Double): Double = train.mass * G * (grade / 1000.0) // grade en ‰

    private fun computeDavisResistance(train: Train) : Double {

        return train.davisA + train.davisB * train.velocity + train.davisC * train.velocity * train.velocity

    }

    private fun computeTraction(train: Train, throttle: Double): Double {
        //F_t = min(F_t_max,  P_max / max(v, ε))  ×  throttle

        val dominantLimitation = minOf(train.maxTractiveEffort, train.maxPower / maxOf(train.velocity, EPSILON))
        return dominantLimitation * throttle

    }

    private fun computeNetForce(train: Train, command: DriveCommand, conditions: TrackConditions): Double {
        val tractionForce = computeTraction(train, command.throttle)
        val brakingForce = command.brake * train.maxBrakingForce
        val gradeForce = computeGradeForce(train, conditions.grade)
        val davisResistance = computeDavisResistance(train)

        return tractionForce - brakingForce - gradeForce - davisResistance
    }

    private fun integrate(train: Train, netForce: Double, dt: Double) {
        // F = m * a  =>  a = F / m
        val acceleration = netForce / (train.mass * train.rotatingMassFactor)
        // Integración semi-implícita de Euler. La que más me gusta es la del promedio, pero esta simplifica el tema de detener el tren.
        (train.velocity + acceleration * dt).coerceIn(0.0, train.maxSpeed)
        // Limitar la velocidad a maxSpeed y no permitir velocidades negativas
        train.position += train.velocity * dt
    }

}