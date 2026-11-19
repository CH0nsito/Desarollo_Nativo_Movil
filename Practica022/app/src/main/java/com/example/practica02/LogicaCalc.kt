package com.example.practica02

object LogicaCalc {

    fun calcular(num1: String, num2: String, operation: String): String {
        val n1 = num1.toDoubleOrNull() ?: return ""
        val n2 = num2.toDoubleOrNull() ?: return ""

        val result = when (operation) {
            "+" -> n1 + n2
            "-" -> n1 - n2
            "x" -> n1 * n2
            "÷" -> if (n2 != 0.0) n1 / n2 else Double.NaN
            else -> 0.0
        }

        return if (result % 1.0 == 0.0) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    }

    fun calcularPorcentaje(num: String): String {
        val n = num.toDoubleOrNull() ?: return ""
        val result = n / 100
        return if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
    }
}