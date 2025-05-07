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

# Explicacion de la salida del proyecto:

# 1. Cargar y Preprocesar Datos
Cuando eliges la opción 1. Cargar y preprocesar datos, el programa:

Lee el archivo heart.csv desde la carpeta data/.

Omite la primera fila, que corresponde a los nombres de las columnas.

Lee cada línea como un paciente y transforma los datos categóricos en valores numéricos (codificación manual).

Filtra registros inválidos, asegurándose de que todas las variables estén correctamente codificadas.

# ¿Qué significa la salida?
```
yaml
Total de registros: 918, válidos: 918
```
Esto te dice dos cosas: 

Total de registros: Se encontraron 918 filas en el archivo heart.csv, lo cual es correcto y coincide con el dataset original de Kaggle llamado Heart Failure Prediction Dataset:
🔗 https://www.kaggle.com/datasets/fedesoriano/heart-failure-prediction

Válidos: 918: Significa que no hubo registros con datos erróneos o mal codificados, es decir:

Todas las columnas categóricas (Sex, ChestPainType, RestingECG, ST_Slope, etc.) se codificaron correctamente.

No hay valores nulos o no reconocidos.

Por tanto, todos los registros son utilizables para el modelo.

# ¿Qué preprocesamiento se hizo?
Estas son las transformaciones clave:

Columna original	Transformación realizada
```
Sex	"M" → 1, "F" → 0
ChestPainType	"ATA" → 0, "NAP" → 1, "ASY" → 2, "TA" → 3
RestingECG	"Normal" → 0, "ST" → 1, "LVH" → 2
ExerciseAngina	"Y" → 1, "N" → 0
ST_Slope	"Up" → 0, "Flat" → 1, "Down" → 2
```
Además, se eliminan (filtran) pacientes con valores "-1" (que marcan codificación inválida). Pero en tu caso, ninguno de los 918 registros fue descartado, lo cual es excelente.

# 2. División en Entrenamiento y Prueba (Train/Test Split)

# ¿Qué significa?
En aprendizaje automático, necesitamos evaluar qué tan bien generaliza un modelo. Para lograrlo, dividimos el dataset original en dos partes:

Conjunto	Uso principal
Entrenamiento	Usado para entrenar el modelo: es donde el algoritmo "aprende".
Prueba	Usado para evaluar el modelo con datos no vistos durante el entrenamiento.

# ¿Cómo se divide?
En tu código, al llamar a modeloML.entrenar(pacientesLimpios) se realiza internamente:

```
kotlin

val tamañoEntrenamiento = (datos.size * 0.8).toInt()
val entrenamiento = datos.take(tamañoEntrenamiento)
val prueba = datos.drop(tamañoEntrenamiento)
```
Eso quiere decir:

Se toma el 80% de los pacientes (aproximadamente 734 pacientes) para entrenamiento.

El 20% restante (aproximadamente 184 pacientes) se reserva como conjunto de prueba.

Este tipo de división es estándar y permite medir si el modelo está sobreajustado o si realmente generaliza bien.

# ¿Qué significa el mensaje de salida?
```nginx
Los datos ya están listos para ser usados.
```
Esto no está haciendo una división real en ese momento, sino que simplemente confirma que los datos están limpios y listos para usar. La división en sí ocurre en el paso 3 (cuando entrenas el modelo). El mensaje puede parecer un poco confuso porque no se hace una acción directa aquí, pero sirve para asegurarte de que ya tienes datos válidos en memoria.

# 3. ¿Qué significa “entrenar el modelo”?
Entrenar un modelo significa que el algoritmo de aprendizaje automático (en tu caso, la regresión logística) analiza los datos de entrenamiento (80% del total) para aprender patrones que permitan predecir si un nuevo paciente tiene o no enfermedad cardíaca.

Concretamente:

Usa los datos de entrada (xTrain) y las etiquetas reales (yTrain) para ajustar los coeficientes del modelo.

Después de entrenarse, el modelo puede hacer predicciones sobre datos no vistos, como el conjunto de prueba o pacientes ingresados manualmente.

# Resultado final
Después del mensaje de SLF4J, el programa imprime:

```
nginx
Modelo entrenado correctamente.
```
Eso significa que:

El modelo fue ajustado con éxito usando los datos.

Está listo para hacer predicciones y para que se evalúen sus métricas.

¿Qué son las métricas de desempeño?
Las métricas de desempeño sirven para medir la calidad del modelo al hacer predicciones. Se calculan comparando:

Las predicciones del modelo (yPred).

Los resultados reales del conjunto de prueba (yReal).

Estas métricas indican qué tan preciso, confiable y útil es el modelo para diagnosticar enfermedades cardíacas.

# Análisis de tus métricas:
matlab
Copiar
Editar
MÉTRICAS DEL MODELO
Accuracy : 84,24 %
Precisión: 83,81 %
Recall   : 88,00 %
F1 Score : 85,85 %
Veamos cada una:

# Accuracy (Exactitud): 84,24 %
Indica cuántas predicciones totales fueron correctas (positivas o negativas).

En tu caso, el modelo acierta el 84% de las veces en general.

Fórmula:

Accuracy
=
𝑇
𝑃
+
𝑇
𝑁
𝑇
𝑜
𝑡
𝑎
𝑙
Accuracy= 
Total
TP+TN
​
 
# Precisión (Precision): 83,81 %
Mide cuántos de los casos predichos como positivos realmente lo eran.

Importa cuando queremos minimizar falsos positivos (diagnosticar enfermedad cuando no la hay).

En medicina, esto es clave para no alarmar a pacientes sanos.

# Recall (Sensibilidad): 88,00 %
Mide cuántos de los pacientes que realmente tienen la enfermedad fueron detectados por el modelo.

Importa mucho cuando no queremos que se escape ningún enfermo (minimizar falsos negativos).

# F1 Score: 85,85 %
Es el promedio armónico entre precisión y recall.

Sirve como medida equilibrada si te importan tanto los falsos positivos como los falsos negativos.

Cuanto más alto, mejor balance entre ambos.

# Interpretación médica del modelo
El modelo:

Detecta bien a los enfermos (88% recall).

Comete pocos errores en positivos falsos (84% precisión).

Tiene una exactitud general alta (84%).

Es un modelo bien equilibrado y bastante fiable para diagnóstico preliminar.

# Campos de ingresar para la predicción manual
Cuando ejecutas la opción 5, el programa te pide los siguientes datos:

# 1. Edad (age)
Tipo: Entero (por ejemplo, 45)

Qué es: Edad del paciente en años.

Por qué importa: A mayor edad, mayor riesgo de enfermedad cardíaca.

# 2. Sexo (sex)
Tipo: 1 para Hombre, 0 para Mujer

Qué es: Sexo biológico del paciente.

Por qué importa: Los hombres tienen un riesgo mayor de enfermedad cardíaca a edades más tempranas.

# 3. Tipo de dolor en el pecho (chestPain)
Valores posibles:

0 = Angina típica (ATA)

1 = Angina atípica (NAP)

2 = Dolor no anginoso (ASY)

3 = Dolor tipo TA (probablemente más grave)

Qué es: Tipo de dolor que experimenta el paciente.

Por qué importa: Algunos tipos de dolor están más relacionados con enfermedad cardíaca.

# 4. Presión arterial en reposo (restingBP)
Tipo: Entero (por ejemplo, 120)

Qué es: Presión arterial en milímetros de mercurio cuando el paciente está en reposo.

Por qué importa: La hipertensión es un factor de riesgo.

# 5. Colesterol (cholesterol)
Tipo: Entero (por ejemplo, 240)

Qué es: Nivel de colesterol en sangre (mg/dL).

Por qué importa: El colesterol alto contribuye a la obstrucción de arterias.

# 6. FastingBS (nivel de azúcar en ayunas)
Tipo: 1 si el nivel de glucosa en ayunas es > 120 mg/dL, 0 en caso contrario.

Qué es: Glucosa en sangre sin haber comido.

Por qué importa: Altos niveles indican diabetes, que aumenta el riesgo cardíaco.

# 7. RestECG (electrocardiograma en reposo)
Valores:

0 = Normal

1 = Anormalidad en ST-T

2 = Hipertrofia ventricular izquierda (LVH)

Qué es: Resultado del electrocardiograma mientras el paciente está en reposo.

Por qué importa: Puede mostrar señales de problemas cardíacos.

# 8. Frecuencia cardíaca máxima (maxHR)
Tipo: Entero (por ejemplo, 160)

Qué es: Ritmo máximo alcanzado durante una prueba de esfuerzo.

Por qué importa: Un corazón sano suele alcanzar una buena frecuencia bajo esfuerzo.

# 9. Angina por ejercicio (exerciseAngina)
Tipo: 1 = Sí, 0 = No

Qué es: Si el paciente experimenta dolor en el pecho al hacer ejercicio.

Por qué importa: Puede indicar flujo sanguíneo restringido al corazón.

# 10. Oldpeak (nivel de depresión ST)
Tipo: Número decimal (por ejemplo, 1.4)

Qué es: Diferencia entre el nivel ST en reposo y en esfuerzo (del ECG).

Por qué importa: Valores altos indican problemas en la respuesta del corazón al estrés.

# 11. ST Slope (pendiente del segmento ST)
Valores:

0 = Ascendente (Up)

1 = Plana (Flat)

2 = Descendente (Down)

Qué es: Forma de la curva ST en el electrocardiograma bajo esfuerzo.

Por qué importa: Cambios pueden indicar isquemia u otros problemas.

# Resultado final
Después de ingresar esos datos, el modelo devuelve:

"Positivo" → Probablemente tiene enfermedad cardíaca.

"Negativo" → Probablemente no la tiene.

Esto permite usar el modelo como una herramienta de diagnóstico predictivo básico.
