package io.github.scontreraslopez.trainsim.data

import io.github.scontreraslopez.trainsim.model.Train

/**
 * Catálogo de trenes predefinidos basados en los vehículos del clima Temperate de OpenTTD.
 *
 * Los datos de potencia y masa son los originales del juego; maxSpeed, Davis y frenado
 * son estimaciones físicamente razonables con fines didácticos.
 *
 * Davis calibrado para que la resistencia aerodinámica sea realista por tipo de vehículo.
 * El límite de velocidad se impone como tope mecánico en el integrador (no por equilibrio Davis).
 */
object TrainRepository {

    /**
     * Kirby Paul Tank — vapor, 1925.
     * Locomotora ligera de maniobras y tráfico local.
     * Datos OpenTTD: 300 hp, 47 t, 64 km/h.
     */
    fun kirbyPaulTank(position: Double = 0.0, velocity: Double = 0.0) = Train(
        name                = "Kirby Paul Tank",
        position            = position,
        velocity            = velocity,
        mass                = 47_000.0,
        maxSpeed            = 64.0 / 3.6,       // 17.8 m/s
        maxPower            = 223_710.0,         // 300 hp
        maxTractiveEffort   = 130_000.0,         // ~130 kN  (adherencia ~28% del peso)
        maxBrakingForce     = 100_000.0,
        davisA              = 700.0,
        davisB              = 30.0,
        davisC              = 3.0,
    )

    /**
     * Chaney "Jubilee" — vapor, 1935.
     * Expreso de viajeros de distancia media. Inspirado en la LMS Stanier Jubilee.
     * Datos OpenTTD: ~600 hp, ~78 t, 112 km/h (estimados).
     */
    fun chaneyJubilee(position: Double = 0.0, velocity: Double = 0.0) = Train(
        name                = "Chaney \"Jubilee\"",
        position            = position,
        velocity            = velocity,
        mass                = 78_000.0,
        maxSpeed            = 112.0 / 3.6,      // 31.1 m/s
        maxPower            = 447_420.0,         // 600 hp
        maxTractiveEffort   = 220_000.0,         // ~220 kN
        maxBrakingForce     = 170_000.0,
        davisA              = 1_150.0,
        davisB              = 50.0,
        davisC              = 5.0,
    )

    /**
     * CS 4000 — diésel, ~1963.
     * Locomotora de mercancías pesadas. Inspirada en la Krauss-Maffei ML 4000.
     * Datos OpenTTD: ~4 000 hp, ~150 t, ~120 km/h (estimados).
     */
    fun cs4000(position: Double = 0.0, velocity: Double = 0.0) = Train(
        name                = "CS 4000",
        position            = position,
        velocity            = velocity,
        mass                = 150_000.0,
        maxSpeed            = 120.0 / 3.6,      // 33.3 m/s
        maxPower            = 2_982_800.0,       // 4 000 hp
        maxTractiveEffort   = 500_000.0,         // ~500 kN
        maxBrakingForce     = 400_000.0,
        davisA              = 2_200.0,
        davisB              = 100.0,
        davisC              = 8.0,
    )

    /**
     * Centennial — diésel, ~1972.
     * La locomotora más potente del juego. Inspirada en la Union Pacific Centennial (DD40AX).
     * Datos OpenTTD: ~6 400 hp, ~240 t, ~160 km/h (estimados).
     */
    fun centennial(position: Double = 0.0, velocity: Double = 0.0) = Train(
        name                = "Centennial",
        position            = position,
        velocity            = velocity,
        mass                = 240_000.0,
        maxSpeed            = 160.0 / 3.6,      // 44.4 m/s
        maxPower            = 4_772_480.0,       // 6 400 hp
        maxTractiveEffort   = 800_000.0,         // ~800 kN
        maxBrakingForce     = 600_000.0,
        davisA              = 3_500.0,
        davisB              = 160.0,
        davisC              = 12.0,
    )

    fun all(): List<Train> = listOf(kirbyPaulTank(), chaneyJubilee(), cs4000(), centennial())
}
