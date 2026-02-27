package io.github.scontreraslopez.trainsim.control

import io.github.scontreraslopez.trainsim.model.DrivingContext
import io.github.scontreraslopez.trainsim.model.Train

/** Esta clase todavía por implementar nos dará la posibilidad de acelerar y frenar manualmente a nosotros en tiempo real
 *
 */
class ManualDriver(private val command: DriveCommand) : Driver {
    override fun drive(train: Train, context: DrivingContext) = command
}