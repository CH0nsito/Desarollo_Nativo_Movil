package com.example.practica02

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val CalcBackground = Color(0xFF0D9364)
val DarkScreen = Color(0xFF2C2C2E)
val ButtonColor = Color(0xFF0F9F6C)
val AppBackground = Color(0xFF1B2E4B)

@Composable
fun CalculatorScreen() {
    // Declaración de variables
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var opc by remember { mutableStateOf("") }

    // Función auxiliar para procesar clicks
    fun onButtonClick(simbolo: String) {
        when (simbolo) {
            "C" -> {
                num1 = ""
                num2 = ""
                opc = ""
            }
            "←" -> {
                if (num2.isNotEmpty()) num2 = num2.dropLast(1)
                else if (opc.isNotEmpty()) opc = ""
                else if (num1.isNotEmpty()) num1 = num1.dropLast(1)
            }
            "%" -> {
                if (num1.isNotEmpty() && opc.isEmpty()) {
                    num1 = LogicaCalc.calcularPorcentaje(num1)
                }
            }
            "+", "-", "x", "÷" -> {
                if (num1.isNotEmpty()) opc = simbolo
            }
            "=" -> {
                if (num1.isNotEmpty() && num2.isNotEmpty() && opc.isNotEmpty()) {
                    num1 = LogicaCalc.calcular(num1, num2, opc)
                    num2 = ""
                    opc = ""
                }
            }
            "." -> {
                if (opc.isEmpty() && !num1.contains(".")) {
                    num1 += if (num1.isEmpty()) "0." else "."
                } else if (opc.isNotEmpty() && !num2.contains(".")) {
                    num2 += if (num2.isEmpty()) "0." else "."
                }
            }
            else -> { // Números (0-9)
                if (opc.isEmpty()) {
                    if (num1.length < 8) num1 += simbolo
                } else {
                    if (num2.length < 8) num2 += simbolo
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(CalcBackground)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pantalla
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkScreen)
                    .padding(20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = "$num1 $opc $num2",
                    textAlign = TextAlign.End,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 2
                )
            }

            // Filas de los botones
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Fila 1: C, ←, %
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("C", Modifier.weight(2f)) { onButtonClick("C") }
                    CalcBtn("←", Modifier.weight(1f)) { onButtonClick("←") }
                    CalcBtn("%", Modifier.weight(1f)) { onButtonClick("%") }
                }
                // Fila 2: 7, 8, 9, x
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("7", Modifier.weight(1f)) { onButtonClick("7") }
                    CalcBtn("8", Modifier.weight(1f)) { onButtonClick("8") }
                    CalcBtn("9", Modifier.weight(1f)) { onButtonClick("9") }
                    CalcBtn("x", Modifier.weight(1f)) { onButtonClick("x") }
                }
                // Fila 3: 4, 5, 6, -
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("4", Modifier.weight(1f)) { onButtonClick("4") }
                    CalcBtn("5", Modifier.weight(1f)) { onButtonClick("5") }
                    CalcBtn("6", Modifier.weight(1f)) { onButtonClick("6") }
                    CalcBtn("-", Modifier.weight(1f)) { onButtonClick("-") }
                }
                // Fila 4: 1, 2, 3, +
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("1", Modifier.weight(1f)) { onButtonClick("1") }
                    CalcBtn("2", Modifier.weight(1f)) { onButtonClick("2") }
                    CalcBtn("3", Modifier.weight(1f)) { onButtonClick("3") }
                    CalcBtn("+", Modifier.weight(1f)) { onButtonClick("+") }
                }
                // Fila 5: 0, ., =
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("0", Modifier.weight(2f)) { onButtonClick("0") }
                    CalcBtn(".", Modifier.weight(1f)) { onButtonClick(".") }
                    CalcBtn("=", Modifier.weight(1f)) { onButtonClick("=") }
                }
            }
        }
    }
}

@Composable
fun CalcBtn(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ButtonColor)
            .clickable { onClick() }
    ) {
        Text(text = text, fontSize = 22.sp, color = Color.White)
    }
}