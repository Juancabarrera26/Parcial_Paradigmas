package org.app

import smile.classification.LogisticRegression

class Modelo {
    private var modelo: LogisticRegression? = null
    private lateinit var xTest: Array<DoubleArray>
    private lateinit var yTest: IntArray
    private val normalizador = Normalizador()

    fun entrenar(pacientes: List<Paciente>) {
        val datos = pacientes.shuffled()
        val tamañoEntrenamiento = (datos.size * 0.8).toInt()
        val entrenamiento = datos.take(tamañoEntrenamiento)
        val prueba = datos.drop(tamañoEntrenamiento)

        val (xTrain, yTrain) = convertirAEntradaSmile(entrenamiento)
        val (xPrueba, yPrueba) = convertirAEntradaSmile(prueba)

        normalizador.ajustar(xTrain)
        val xTrainNorm = normalizador.transformar(xTrain)
        val xTestNorm = normalizador.transformar(xPrueba)

        modelo = LogisticRegression.fit(xTrainNorm, yTrain)
        xTest = xTestNorm
        yTest = yPrueba
    }

    fun predecir(): IntArray {
        return xTest.map { modelo?.predict(it) ?: 0 }.toIntArray()
    }

    fun obtenerYReal(): IntArray = yTest

    private fun convertirAEntradaSmile(pacientes: List<Paciente>): Pair<Array<DoubleArray>, IntArray> {
        val features = pacientes.map {
            doubleArrayOf(
                it.age.toDouble(),
                it.sex.toDouble(),
                it.chestPain.toDouble(),
                it.restingBP.toDouble(),
                it.cholesterol.toDouble(),
                it.fastingBS.toDouble(),
                it.restECG.toDouble(),
                it.maxHR.toDouble(),
                it.exerciseAngina.toDouble(),
                it.oldpeak,
                it.stSlope.toDouble()
            )
        }.toTypedArray()

        val labels = pacientes.map { it.target }.toIntArray()
        return Pair(features, labels)
    }
}
