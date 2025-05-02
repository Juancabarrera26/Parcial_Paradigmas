package org.app

import java.io.File

fun cargarPacientes(ruta: String = "data/heart.csv"): List<Paciente> {
    val pacientes = mutableListOf<Paciente>()
    val file = File(ruta)
    val lines = file.readLines().drop(1)

    for (line in lines) {
        val partes = line.split(",")

        val paciente = Paciente(
            age = partes[0].toInt(),
            sex = if (partes[1] == "M") 1 else 0,
            chestPain = when (partes[2]) {
                "ATA" -> 0
                "NAP" -> 1
                "ASY" -> 2
                "TA"  -> 3
                else -> -1
            },
            restingBP = partes[3].toInt(),
            cholesterol = partes[4].toInt(),
            fastingBS = partes[5].toInt(),
            restECG = when (partes[6]) {
                "Normal" -> 0
                "ST"     -> 1
                "LVH"    -> 2
                else -> -1
            },
            maxHR = partes[7].toInt(),
            exerciseAngina = if (partes[8] == "Y") 1 else 0,
            oldpeak = partes[9].toDouble(),
            stSlope = when (partes[10]) {
                "Up"   -> 0
                "Flat" -> 1
                "Down" -> 2
                else -> -1
            },
            target = partes[11].toInt()
        )

        pacientes.add(paciente)
    }

    return pacientes.filter {
        it.chestPain != -1 && it.restECG != -1 && it.stSlope != -1
    }
}
