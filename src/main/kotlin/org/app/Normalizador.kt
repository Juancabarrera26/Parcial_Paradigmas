package org.app

class Normalizador {
    private lateinit var minimos: DoubleArray
    private lateinit var maximos: DoubleArray

    fun ajustar(data: Array<DoubleArray>) {
        val columnas = data[0].size
        minimos = DoubleArray(columnas) { col -> data.minOf { it[col] } }
        maximos = DoubleArray(columnas) { col -> data.maxOf { it[col] } }
    }

    fun transformar(data: Array<DoubleArray>): Array<DoubleArray> {
        return data.map { fila ->
            fila.mapIndexed { i, valor ->
                if (maximos[i] == minimos[i]) 0.0 else (valor - minimos[i]) / (maximos[i] - minimos[i])
            }.toDoubleArray()
        }.toTypedArray()
    }
}
