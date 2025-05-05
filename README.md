# Análisis Predictivo de Enfermedades Cardíacas con Kotlin y Smile

Este proyecto tiene el objetivo principal de aplicar el ciclo completo de análisis de datos utilizando un dataset real relacionado con enfermedades cardíacas. Todo el desarrollo se realizó en **Kotlin**, con el uso de la biblioteca **Smile** para el modelo de clasificación.

---

## Dataset Utilizado

* **Nombre**: Heart Disease UCI
* **Fuente**: Kaggle
* **Enlace**: [https://www.kaggle.com/datasets/fedesoriano/heart-failure-prediction](https://www.kaggle.com/datasets/fedesoriano/heart-failure-prediction)
* **Tamaño**: 918 registros
* **Columnas**: 12 variables clínicas + 1 columna objetivo (presencia de enfermedad cardíaca)

Este dataset contiene información relevante como edad, colesterol, presión arterial, entre otras, para predecir si un paciente desarrollará una enfermedad cardíaca.

---

## Herramientas y Tecnologías

* **Lenguaje**: Kotlin
* **Machine Learning**: Smile (Statistical Machine Intelligence and Learning Engine)
* **Interfaz**: CLI (Interfaz por consola)

---

## Flujo de Trabajo Implementado

### *1. Carga y Preprocesamiento del Dataset*

* Se carga el archivo `heart.csv` desde la carpeta `data/`.
* Se convierten variables categóricas en numéricas para poder entrenar el modelo:

  * `Sex`: M/F → 1/0
  * `ChestPainType`: ATA/NAP/ASY/TA → 0/1/2/3
  * `RestingECG`: Normal/ST/LVH → 0/1/2
  * `ExerciseAngina`: Y/N → 1/0
  * `ST_Slope`: Up/Flat/Down → 0/1/2
* Se eliminan registros con valores no reconocidos (donde la conversión dá -1).

### *2. Normalización de Datos*

* Se aplica normalización Min-Max sobre los atributos numéricos.
* Esto evita que variables con distintas magnitudes afecten negativamente el modelo.

### *3. División del Dataset (Train/Test Split)*

* Se hace un split 80/20: 80% de los datos se usan para entrenamiento, 20% para pruebas.
* Los datos se barajan aleatoriamente para asegurar una representación equilibrada.

### *4. Entrenamiento del Modelo*

* Se entrena un modelo de **Regresión Logística** usando Smile.
* Elegimos este modelo porque es sencillo, interpretable y efectivo para clasificación binaria.

### *5. Evaluación del Modelo*

* Se calculan las siguientes métricas:

  * **Accuracy**: porcentaje total de aciertos
  * **Precision**: exactitud en los positivos predichos
  * **Recall**: proporción de positivos correctamente detectados
  * **F1 Score**: media armónica de precision y recall

Ejemplo de salida por consola:

```
Resultados del modelo
Precisión (Accuracy): 82,07 %
Precision: 78,95 %
Recall: 85,23 %
F1 Score: 81,97 %
```

---

## Interfaz CLI (por consola)

Para interactuar con la aplicación, se diseñó una interfaz por consola con el siguiente menú:

```
Menú Principal
1. Cargar y preprocesar datos
2. Dividir datos en entrenamiento y prueba
3. Entrenar modelo
4. Ver métricas de desempeño
5. Salir
```

Este menú permite al usuario ejecutar el flujo completo paso a paso. Cada opción está pensada para guiar al usuario sin necesidad de modificar el código.

---

## Estructura del Proyecto

```
 project-root
 data
   └─ heart.csv
 src
   ├️ Main.kt
   ├️ Paciente.kt
   ├️ Preprocesamiento.kt
   ├️ Modelo.kt
   └️ Evaluacion.kt
README.md
```

---

##  Explicación de Archivos Fuente

### `Main.kt`

Este es el archivo principal donde se controla todo el flujo del programa mediante un menú interactivo por consola.
Cada opción llama a funciones específicas que se encargan de las distintas fases del análisis:

```kotlin
fun main() {
  // Declaración de variables globales
  var pacientes: List<Paciente> = listOf()
  var pacientesLimpios: List<Paciente> = listOf()
  var modelo: LogisticRegression? = null
  var xTest: Array<DoubleArray> = arrayOf()
  var yTest: IntArray = intArrayOf()
  var predicciones: IntArray = intArrayOf()

  while (true) {
    println("Menú Principal")
    println("1. Cargar y preprocesar datos")
    println("2. Dividir datos en entrenamiento y prueba")
    println("3. Entrenar modelo")
    println("4. Ver métricas de desempeño")
    println("5. Salir")
    print("Seleccione una opción: ")

    when (readLine()?.toIntOrNull()) {
      1 -> { /* Carga del CSV y conversiones */ }
      2 -> { /* Normalización y split */ }
      3 -> { /* Entrenamiento del modelo */ }
      4 -> { /* Métricas y resultados */ }
      5 -> break
      else -> println("Opción inválida.")
    }
  }
}
```

Este archivo actúa como "coordinador" del programa, llamando a funciones de los otros archivos.

## **¿Qué hace este archivo?**

Controla el ciclo completo de ejecución:

1. **Carga del dataset**
2. **Preprocesamiento y limpieza**
3. **División de los datos (entrenamiento y prueba)**
4. **Entrenamiento del modelo**
5. **Evaluación del modelo con métricas**
6. **Interacción con el usuario mediante menú en consola**

---

## **Estructura del código y su explicación**

```kotlin
package org.app

import java.io.File
import smile.classification.LogisticRegression

fun main() {
    var pacientes: List<Paciente> = listOf()
    var pacientesLimpios: List<Paciente> = listOf()
    var modelo: LogisticRegression? = null
    var xTest: Array<DoubleArray> = arrayOf()
    var yTest: IntArray = intArrayOf()
    var predicciones: IntArray = intArrayOf()
```

* Se definen variables globales para almacenar:

  * Los pacientes cargados desde el CSV
  * Una versión limpia sin errores o valores desconocidos
  * El modelo entrenado (usando Smile)
  * Los datos de prueba (X e Y) y sus predicciones

---

### **Menú principal**

```kotlin
    while (true) {
        println("Menú Principal")
        println("1. Cargar datos")
        println("2. Entrenar modelo")
        println("3. Ver métricas")
        println("4. Salir")
        print("Seleccione una opción: ")

        when (readLine()?.toIntOrNull()) {
```

* Muestra un menú simple de opciones para que el usuario pueda elegir qué desea hacer.
* El `when` actúa como un `switch` para ejecutar el bloque correspondiente a cada opción.

---

### **Opcion 1: Cargar datos**

```kotlin
            1 -> {
                val file = File("data/heart.csv")
                val lines = file.readLines().drop(1)
                val lista = mutableListOf<Paciente>()
                for (line in lines) {
                    val partes = line.split(",")
```

* Se abre el archivo CSV.
* Se omite la primera línea (cabecera).
* Se itera sobre cada fila y se convierte en un objeto `Paciente`.

```kotlin
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
                    lista.add(paciente)
                }
```

* Se hace conversión manual de variables categóricas a numéricas.
* Se construyen objetos `Paciente` para cada fila válida.

```kotlin
                pacientes = lista
                pacientesLimpios = pacientes.filter {
                    it.chestPain != -1 && it.restECG != -1 && it.stSlope != -1
                }

                println("Pacientes cargados: ${pacientes.size}")
                println("Pacientes válidos: ${pacientesLimpios.size}")
            }
```

* Se filtran registros con errores (-1).
* Se informa al usuario sobre los registros cargados y válidos.

---

### **Opcion 2: Entrenar modelo**

```kotlin
            2 -> {
                if (pacientesLimpios.isEmpty()) {
                    println("Primero debe cargar los datos.")
                    continue
                }
```

* Verifica que haya datos limpios cargados.

```kotlin
                val datos = pacientesLimpios.shuffled()
                val tamañoEntrenamiento = (datos.size * 0.8).toInt()
                val entrenamiento = datos.take(tamañoEntrenamiento)
                val prueba = datos.drop(tamañoEntrenamiento)
```

* Divide aleatoriamente en 80% entrenamiento y 20% prueba.

```kotlin
                fun convertirAEntradaSmile(pacientes: List<Paciente>): Pair<Array<DoubleArray>, IntArray> {
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
```

* Convierte la lista de objetos `Paciente` en arreglos para Smile: `Array<DoubleArray>` para los atributos y `IntArray` para los targets.

```kotlin
                val (xTrain, yTrain) = convertirAEntradaSmile(entrenamiento)
                val (xPrueba, yPrueba) = convertirAEntradaSmile(prueba)

                modelo = LogisticRegression.fit(xTrain, yTrain)
                xTest = xPrueba
                yTest = yPrueba
                predicciones = xTest.map { modelo.predict(it) }.toIntArray()

                println("Modelo entrenado correctamente.")
            }
```

* Se entrena un modelo de regresión logística.
* Se generan predicciones con los datos de prueba.

---

### **Opcion 3: Ver métricas**

```kotlin
            3 -> {
                if (modelo == null || yTest.isEmpty() || predicciones.isEmpty()) {
                    println("Primero debe entrenar el modelo.")
                    continue
                }
```

* Verifica que se haya entrenado un modelo antes de evaluar.

```kotlin
                fun calcularAccuracy(yTrue: IntArray, yPred: IntArray): Double {
                    val correctos = yTrue.indices.count { yTrue[it] == yPred[it] }
                    return correctos.toDouble() / yTrue.size
                }
```

* Calcula el porcentaje de aciertos.

```kotlin
                fun calcularPrecision(yTrue: IntArray, yPred: IntArray): Double {
                    val tp = yTrue.indices.count { yPred[it] == 1 && yTrue[it] == 1 }
                    val fp = yTrue.indices.count { yPred[it] == 1 && yTrue[it] == 0 }
                    return if (tp + fp == 0) 0.0 else tp.toDouble() / (tp + fp)
                }
```

* Calcula la precisión (positivos verdaderos sobre predicciones positivas).

```kotlin
                fun calcularRecall(yTrue: IntArray, yPred: IntArray): Double {
                    val tp = yTrue.indices.count { yPred[it] == 1 && yTrue[it] == 1 }
                    val fn = yTrue.indices.count { yPred[it] == 0 && yTrue[it] == 1 }
                    return if (tp + fn == 0) 0.0 else tp.toDouble() / (tp + fn)
                }
```

* Calcula la sensibilidad o recall (positivos verdaderos sobre todos los reales).

```kotlin
                fun calcularF1(precision: Double, recall: Double): Double {
                    return if (precision + recall == 0.0) 0.0 else 2 * (precision * recall) / (precision + recall)
                }
```

* Calcula la métrica F1: media armónica entre precisión y recall.

```kotlin
                val accuracy = calcularAccuracy(yTest, predicciones)
                val precision = calcularPrecision(yTest, predicciones)
                val recall = calcularRecall(yTest, predicciones)
                val f1 = calcularF1(precision, recall)

                println("Resultados del modelo")
                println("Precisión (Accuracy): %.2f %%".format(accuracy * 100))
                println("Precision: %.2f %%".format(precision * 100))
                println("Recall: %.2f %%".format(recall * 100))
                println("F1 Score: %.2f %%".format(f1 * 100))
            }
```

* Se muestran los resultados en consola de forma legible para el usuario.

---

### **Opcion 4: Salir**

```kotlin
            4 -> {
                println("Saliendo del programa.")
                break
            }
            else -> println("Opción inválida.")
        }
    }
}
```

* Cierra el ciclo infinito `while` y termina el programa.
* Cualquier otra entrada fuera de 1-4 es considerada inválida.

---

### `Paciente.kt`

Define una clase `Paciente` con los atributos del dataset. Sirve como estructura para representar cada fila del archivo CSV de forma tipada.

```kotlin
data class Paciente(
  val age: Int,
  val sex: Int,
  val chestPain: Int,
  val restingBP: Int,
  val cholesterol: Int,
  val fastingBS: Int,
  val restECG: Int,
  val maxHR: Int,
  val exerciseAngina: Int,
  val oldpeak: Double,
  val stSlope: Int,
  val target: Int
)
```

### `Preprocesamiento.kt`

Incluye funciones para:

* Normalizar datos (Min-Max scaling)
* Dividir en conjunto de entrenamiento y prueba
* Convertir listas de `Paciente` en arrays de Smile (`Array<DoubleArray>` y `IntArray`)

### `Modelo.kt`

Contiene funciones para:

* Entrenar el modelo de regresión logística
* Realizar predicciones sobre datos nuevos

### `Evaluacion.kt`

Calcula las métricas (accuracy, precision, recall, F1). Cada métrica está implementada a mano para entender su funcionamiento.

---

## Decisiones de Diseño y Justificación

* **Modelo elegido**: Regresión Logística. Es un modelo fácil de interpretar y funciona bien para clasificación binaria.
* **Preprocesamiento**: Se normalizaron los datos y se codificaron las variables categóricas. Esto mejora el rendimiento y compatibilidad del modelo.
* **Estructura modular**: Separamos la lógica en varios archivos para mantener claridad.

---

## Posibles Mejoras Futuras

* Añadir validación cruzada para mayor robustez.
* Implementar más modelos y comparar resultados (KNN, árboles de decisión, etc.).
* Crear una interfaz gráfica (GUI) con Kotlin y Jetpack Compose o TornadoFX.

---

## Requisitos del sistema

* JDK 17 o superior
* Kotlin 1.9+
* Smile library (ya incluida en el build)
* Editor recomendado: IntelliJ IDEA

---

## Referencias

* Dataset original: Kaggle → [Heart Failure Prediction](https://www.kaggle.com/datasets/fedesoriano/heart-failure-prediction)
* Documentación de Smile: [https://haifengl.github.io](https://haifengl.github.io)
