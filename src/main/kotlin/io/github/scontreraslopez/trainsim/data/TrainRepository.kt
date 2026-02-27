package io.github.scontreraslopez.trainsim.data

import io.github.scontreraslopez.trainsim.model.Train

/**
 * Catálogo de trenes predefinidos.
 *
 * Incluye vehículos de OpenTTD (clima Temperate) y material rodante real de Renfe.
 * Para los OpenTTD, la potencia y masa son del juego; el resto son estimaciones físicas.
 * Para los Renfe, los datos de potencia, masa y velocidad son reales (fuentes Wikipedia /
 * listadotren.es); Davis y frenado son estimaciones calibradas para fines didácticos.
 *
 * Criterio Davis: coeficiente C ajustado para que el equilibrio de fuerzas (F_t = F_Davis)
 * se produzca en torno a maxSpeed. El tope mecánico del integrador actúa como respaldo.
 */
class TrainRepository {

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

    /**
     * Renfe Serie 592 "Camello" — diésel, 1983. Cercanías y media distancia.
     * Unidad empleada en la línea Murcia–Alicante (C-1) y otras líneas sin electrificar.
     * Datos reales: ~412 kW, ~120 km/h. Peso estimado ~55 t (unidad bimotora).
     * Fuente: listadotren.es / Wikipedia Serie 596 (familia 592/593/596).
     */
    fun renfe592Camello(position: Double = 0.0, velocity: Double = 0.0) = Train(
        name                = "Renfe 592 \"Camello\"",
        position            = position,
        velocity            = velocity,
        mass                = 55_000.0,
        maxSpeed            = 120.0 / 3.6,      // 33.3 m/s
        maxPower            = 412_000.0,         // 412 kW (360 CV nominales)
        maxTractiveEffort   = 80_000.0,          // ~80 kN estimado
        maxBrakingForce     = 60_000.0,
        davisA              = 550.0,
        davisB              = 45.0,
        davisC              = 2.5,               // equilibrio ~180 km/h → maxSpeed clamp a 120
    )

    /**
     * Renfe S-102 "Talgo 350" — eléctrico, 2005. AVE larga distancia.
     * Datos reales: 8 000 kW, 332 t (vacío) / 357 t (cargado), 330 km/h comercial.
     * Davis calibrado para equilibrio F_t ≈ F_Davis en torno a 330 km/h:
     *   C = (P/v_max - A - B·v_max) / v_max² ≈ 8.3 N·s²/m²
     * Fuente: Wikipedia Renfe Clase 102 / listadotren.es.
     */
    fun renfeS102Talgo350(position: Double = 0.0, velocity: Double = 0.0) = Train(
        name                = "Renfe S-102 Talgo 350",
        position            = position,
        velocity            = velocity,
        mass                = 340_000.0,         // media entre vacío (332 t) y cargado (357 t)
        maxSpeed            = 330.0 / 3.6,       // 91.7 m/s
        maxPower            = 8_000_000.0,        // 8 000 kW (2 × 4 000 kW)
        maxTractiveEffort   = 400_000.0,          // ~400 kN estimado
        maxBrakingForce     = 500_000.0,          // freno de disco + magnético a alta velocidad
        davisA              = 3_400.0,
        davisB              = 150.0,
        davisC              = 8.3,               // calibrado para equilibrio ~330 km/h
    )

    fun all(): List<Train> = listOf(
        kirbyPaulTank(), chaneyJubilee(), cs4000(), centennial(),
        renfe592Camello(), renfeS102Talgo350()
    )
}
