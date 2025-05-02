package org.app

fun calcularMetricas(yReal: IntArray, yPred: IntArray) {
    var tp = 0
    var tn = 0
    var fp = 0
    var fn = 0

    for (i in yReal.indices) {
        when {
            yReal[i] == 1 && yPred[i] == 1 -> tp++
            yReal[i] == 0 && yPred[i] == 0 -> tn++
            yReal[i] == 0 && yPred[i] == 1 -> fp++
            yReal[i] == 1 && yPred[i] == 0 -> fn++
        }
    }

    val precision = if (tp + fp > 0) tp.toDouble() / (tp + fp) else 0.0
    val recall = if (tp + fn > 0) tp.toDouble() / (tp + fn) else 0.0
    val f1 = if (precision + recall > 0) 2 * (precision * recall) / (precision + recall) else 0.0
    val accuracy = (tp + tn).toDouble() / yReal.size

    println("Métricas del Modelo")
    println("Precisión: %.2f".format(precision))
    println("Recall: %.2f".format(recall))
    println("F1 Score: %.2f".format(f1))
    println("Accuracy: %.2f".format(accuracy))
}
