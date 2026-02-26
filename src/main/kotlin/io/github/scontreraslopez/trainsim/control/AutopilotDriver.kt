package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.DrivingContext
import io.github.scontreraslopez.trainsim.model.Train

class AutopilotDriver : Driver {

    companion object {
        private const val BRAKE_DISTANCE = 5000.0       // m — distancia al stop donde comienza el frenado de aproximación
        private const val FINAL_BRAKE_DISTANCE = 100.0  // m — distancia al stop donde comienza el frenado de parada
        private const val KP = 0.5                      // ganancia proporcional del controlador de crucero (TBD)
        private const val STOP_VELOCITY = 0.5           // m/s — umbral de "parado"
    }

    enum class Phase {
        STOPPED,      // brake = 1, throttle = 0
        ACCELERATING, // throttle = 1, brake = 0
        CRUISING,     // throttle = Kp*error, brake = 0
        COASTING,     // throttle = 0, brake = 0
        BRAKING       // throttle = 0, brake > 0
    }

    var phase: Phase = Phase.STOPPED
        private set

    override fun drive(train: Train, context: DrivingContext): DriveCommand {
        val targetCruisingSpeed = minOf(context.segment.lineSpeedLimit, train.maxSpeed)

        // Transiciones de fase
        phase = when (phase) {
            Phase.STOPPED -> Phase.ACCELERATING  // salida autorizada inmediata (sin tiempo de andén)

            Phase.ACCELERATING -> when {
                train.velocity >= targetCruisingSpeed -> Phase.CRUISING
                else -> Phase.ACCELERATING
            }

            Phase.CRUISING -> when {
                // Frenado final: últimos 100 m independientemente de la velocidad
                context.distanceToNextStop <= FINAL_BRAKE_DISTANCE -> Phase.BRAKING
                // Frenado de aproximación: dentro del rango de frenado y aún más rápido que la velocidad de entrada
                context.distanceToNextStop <= BRAKE_DISTANCE &&
                        train.velocity > context.nextStopApproachSpeed -> Phase.BRAKING
                else -> Phase.CRUISING
            }

            Phase.BRAKING -> when {
                // Parada válida: velocidad casi nula dentro del margen de andén
                context.isInStopZone && train.velocity <= STOP_VELOCITY -> Phase.STOPPED
                // Frenado de aproximación completado: velocidad baja suficiente para crucero de aproximación
                context.distanceToNextStop > FINAL_BRAKE_DISTANCE &&
                        train.velocity <= context.nextStopApproachSpeed -> Phase.CRUISING
                else -> Phase.BRAKING
            }

            Phase.COASTING -> Phase.COASTING  // reservado
        }

        // Comandos para la fase activa
        return when (phase) {
            Phase.STOPPED -> DriveCommand.FULL_BRAKE

            Phase.ACCELERATING -> DriveCommand.FULL_THROTTLE

            Phase.CRUISING -> {
                // En zona de aproximación, el objetivo es la velocidad de entrada a la estación
                val targetSpeed = if (context.isInApproachZone) {
                    context.nextStopApproachSpeed
                } else {
                    targetCruisingSpeed
                }
                val throttle = ((targetSpeed - train.velocity) * KP).coerceIn(0.0, 1.0)
                DriveCommand(throttle, 0.0)
            }

            Phase.BRAKING -> {
                if (context.distanceToNextStop <= FINAL_BRAKE_DISTANCE) {
                    // Frenado de parada: freno proporcional a la velocidad actual (objetivo = 0)
                    val brake = (train.velocity / train.maxSpeed).coerceIn(0.0, 1.0)
                    DriveCommand(0.0, brake)
                } else {
                    // Frenado de aproximación moderado al 50 %
                    DriveCommand(0.0, 0.5)
                }
            }

            Phase.COASTING -> DriveCommand.COAST
        }
    }
}
