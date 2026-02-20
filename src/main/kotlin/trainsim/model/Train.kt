package io.github.scontreraslopez.trainsim.model

/**
 * Representa el tren con su estado dinámico y sus parámetros físicos fijos.
 *
 * Estado mutable (actualizado en cada paso de simulación):
 * @property position Posición en m sobre la ruta. Double para acumulación sub-métrica en integración.
 * @property velocity Velocidad en m/s.
 *
 * Parámetros de tracción y frenado (H2, H3 del README):
 * @property maxPower Potencia máxima de tracción en W.
 * @property maxTractiveEffort Esfuerzo máximo de tracción en N (límite por adherencia).
 * @property maxBrakingForce Fuerza máxima de frenado en N.
 *
 * Coeficientes de resistencia Davis (H4 del README):
 * @property davisA Término constante en N (rodadura).
 * @property davisB Término lineal en N·s/m (rozamiento de pestaña).
 * @property davisC Término cuadrático en N·s²/m² (arrastre aerodinámico).
 *
 * @property mass Masa total del tren en kg.
 * @property rotatingMassFactor Factor de masa rotante ξ, adimensional (H6 del README).
 */
class Train(
    var position: Double,
    var velocity: Double,
    val mass: Double,
    val maxSpeed: Double,            // m/s — límite mecánico/electrónico del vehículo
    val maxPower: Double,            // W
    val maxTractiveEffort: Double,   // N
    val maxBrakingForce: Double,     // N
    val davisA: Double,              // N
    val davisB: Double,              // N·s/m
    val davisC: Double,              // N·s²/m²
    val rotatingMassFactor: Double = 1.06
) {
    fun velocityKmH(): Double = velocity * 3.6

    override fun toString(): String =
        "Train(position=%.0fm [%.2fkm], velocity=%.2fm/s [%.1fkm/h], mass=%.0fkg)"
            .format(position, position / 1000.0, velocity, velocityKmH(), mass)
}
