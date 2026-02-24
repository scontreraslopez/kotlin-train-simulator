package io.github.scontreraslopez.trainsim.ui

/**
 * Menú de consola genérico.
 *
 * No conoce el dominio: trabaja exclusivamente con cadenas de texto
 * e índices. El llamador es responsable de mapear el índice devuelto
 * al objeto de dominio correspondiente.
 */
object ConsoleMenu {

    fun greet() {
        println("¡Bienvenido al simulador de trenes!")
        println("Selecciona un tren y un escenario para comenzar la simulación.")
    }

    /**
     * Muestra [options] numeradas y espera una selección válida del usuario.
     *
     * @return índice base-0 de la opción elegida.
     */
    fun select(prompt: String, options: List<String>): Int {
        require(options.isNotEmpty()) { "La lista de opciones no puede estar vacía" }

        println()
        println(prompt)
        options.forEachIndexed { i, label -> println("  ${i + 1}. $label") }

        while (true) {
            print("> ")
            val input = readlnOrNull()?.trim()?.toIntOrNull()
            if (input != null && input in 1..options.size) return input - 1
            println("Opción no válida. Introduce un número entre 1 y ${options.size}.")
        }
    }
}
