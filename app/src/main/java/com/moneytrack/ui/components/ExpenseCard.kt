package com.moneytrack.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ExpenseCard() {
    Text(text = "Hello World")
}

@Preview(showBackground = true)
@Composable
fun ExpenseCardPreview() {
    ExpenseCard()
}
