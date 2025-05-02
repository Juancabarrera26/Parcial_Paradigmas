package org.app

import java.io.File
import smile.classification.LogisticRegression

fun main() {
    var pacientes: List<Paciente> = listOf()
    var pacientesLimpios: List<Paciente> = listOf()
    var datosNormalizados: List<Paciente> = listOf()
    var xTest: Array<DoubleArray> = arrayOf()
    var yTest: IntArray = intArrayOf()
    var predicciones: IntArray = intArrayOf()
    var modelo: LogisticRegression? = null

    while (true) {
        println("Menú Principal")
        println("1. Cargar y preprocesar datos")
        println("2. Dividir datos en entrenamiento y prueba")
        println("3. Entrenar modelo")
        println("4. Ver métricas de desempeño")
        println("5. Salir")
        print("Seleccione una opción: ")

        when (readLine()?.toIntOrNull()) {
            1 -> {
                val file = File("data/heart.csv")
                val lines = file.readLines().drop(1)

                val lista = mutableListOf<Paciente>()
                for (line in lines) {
                    val partes = line.split(",")
                    val paciente = Paciente(
                        age = partes[0].toInt(),
                        sex = if (partes[1] == "M") 1 else 0,
                        chestPain = when (partes[2]) {
                            "ATA" -> 0; "NAP" -> 1; "ASY" -> 2; "TA" -> 3
                            else -> -1
                        },
                        restingBP = partes[3].toInt(),
                        cholesterol = partes[4].toInt(),
                        fastingBS = partes[5].toInt(),
                        restECG = when (partes[6]) {
                            "Normal" -> 0; "ST" -> 1; "LVH" -> 2
                            else -> -1
                        },
                        maxHR = partes[7].toInt(),
                        exerciseAngina = if (partes[8] == "Y") 1 else 0,
                        oldpeak = partes[9].toDouble(),
                        stSlope = when (partes[10]) {
                            "Up" -> 0; "Flat" -> 1; "Down" -> 2
                            else -> -1
                        },
                        target = partes[11].toInt()
                    )
                    lista.add(paciente)
                }

                pacientes = lista
                pacientesLimpios = pacientes.filter {
                    it.chestPain != -1 && it.restECG != -1 && it.stSlope != -1
                }
                println("Pacientes cargados: ${pacientes.size}")
                println("Pacientes válidos: ${pacientesLimpios.size}")
            }

            2 -> {
                if (pacientesLimpios.isEmpty()) {
                    println("Debe cargar los datos primero.")
                    continue
                }

                fun normalizar(lista: List<Double>): List<Double> {
                    val min = lista.minOrNull() ?: 0.0
                    val max = lista.maxOrNull() ?: 1.0
                    return lista.map { (it - min) / (max - min) }
                }

                val edades = normalizar(pacientesLimpios.map { it.age.toDouble() })
                val presiones = normalizar(pacientesLimpios.map { it.restingBP.toDouble() })
                val colesteroles = normalizar(pacientesLimpios.map { it.cholesterol.toDouble() })
                val frecuencias = normalizar(pacientesLimpios.map { it.maxHR.toDouble() })
                val oldpeaks = normalizar(pacientesLimpios.map { it.oldpeak })

                datosNormalizados = pacientesLimpios.mapIndexed { i, p ->
                    p.copy(
                        age = (edades[i] * 100).toInt(),
                        restingBP = (presiones[i] * 100).toInt(),
                        cholesterol = (colesteroles[i] * 100).toInt(),
                        maxHR = (frecuencias[i] * 100).toInt(),
                        oldpeak = oldpeaks[i]
                    )
                }

                println("Datos normalizados y listos para dividir.")
            }

            3 -> {
                if (datosNormalizados.isEmpty()) {
                    println("Debe normalizar y dividir los datos primero.")
                    continue
                }

                val datos = datosNormalizados.shuffled()
                val split = (datos.size * 0.8).toInt()
                val entrenamiento = datos.take(split)
                val prueba = datos.drop(split)

                fun convertir(pacientes: List<Paciente>): Pair<Array<DoubleArray>, IntArray> {
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

                val (xTrain, yTrain) = convertir(entrenamiento)
                val (xVal, yVal) = convertir(prueba)

                modelo = LogisticRegression.fit(xTrain, yTrain)
                xTest = xVal
                yTest = yVal
                predicciones = xTest.map { modelo.predict(it) }.toIntArray()

                println("Modelo entrenado correctamente.")
            }

            4 -> {
                if (modelo == null || yTest.isEmpty()) {
                    println("Debe entrenar el modelo primero.")
                    continue
                }

                fun accuracy(yTrue: IntArray, yPred: IntArray): Double =
                    yTrue.indices.count { yTrue[it] == yPred[it] }.toDouble() / yTrue.size

                fun precision(yTrue: IntArray, yPred: IntArray): Double {
                    val tp = yTrue.indices.count { yPred[it] == 1 && yTrue[it] == 1 }
                    val fp = yTrue.indices.count { yPred[it] == 1 && yTrue[it] == 0 }
                    return if (tp + fp == 0) 0.0 else tp.toDouble() / (tp + fp)
                }

                fun recall(yTrue: IntArray, yPred: IntArray): Double {
                    val tp = yTrue.indices.count { yPred[it] == 1 && yTrue[it] == 1 }
                    val fn = yTrue.indices.count { yPred[it] == 0 && yTrue[it] == 1 }
                    return if (tp + fn == 0) 0.0 else tp.toDouble() / (tp + fn)
                }

                fun f1(p: Double, r: Double): Double =
                    if (p + r == 0.0) 0.0 else 2 * p * r / (p + r)

                val acc = accuracy(yTest, predicciones)
                val prec = precision(yTest, predicciones)
                val rec = recall(yTest, predicciones)
                val f1score = f1(prec, rec)

                println("\nResultados del modelo")
                println("Precisión (Accuracy): %.2f %%".format(acc * 100))
                println("Precision: %.2f %%".format(prec * 100))
                println("Recall: %.2f %%".format(rec * 100))
                println("F1 Score: %.2f %%".format(f1score * 100))
            }

            5 -> {
                println("Saliendo del programa.")
                break
            }

            else -> println("Opción inválida.")
        }
    }
}
