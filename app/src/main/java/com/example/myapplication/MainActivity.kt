package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import java.util.Stack

class MainActivity : AppCompatActivity() {
    lateinit var textView01: TextView
    lateinit var textView02: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        textView01 = findViewById(R.id.textView01)
        textView02 = findViewById(R.id.textView02)
    }

    var input: String = ""
    var temp: String = "0"

    fun clickNumber(view: View) {
        val thisButton = view as Button
        input += thisButton.text.toString()
        temp += thisButton.text.toString()
        textView01.text = input
    }

    fun clickOperator(view: View) {
        val thisButton = view as Button
        var operator = thisButton.text.toString()
        if (operator == "X") operator = "*"
        if (temp.isDigitsOnly()) {
            input += operator
            temp = operator
            textView01.text = input
        }
    }

    fun clickAC(view: View) {
        temp = ""
        input = ""
        textView01.text = input
    }

    fun clickC(view: View) {
        if (temp.isDigitsOnly()) {
            input = input.substring(0, input.length - temp.length)
            textView01.text = input
            temp = ""
        }
    }

    fun clickBS(view: View) {
        if (input.isNotEmpty()) {
            val lastChar: Char = input.last()
            input = input.substring(0, input.length - 1)
            textView01.text = input
            temp = if (lastChar.isDigit()) temp.substring(0, temp.length - 1) else "0"
        }
    }

    fun clickBang(view: View) {
        if (input.isNotEmpty()) {
            try {
                val result = evaluatePostfix(convertToPostfix(input))
                textView02.text = result
                input = result
                temp = input
            } catch (e: Exception) {
                textView02.text = "ERR"
                input = ""
                temp = "0"
            }
        }
    }

    fun convertToPostfix(expression: String): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<Char>()
        val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2)

        var number = ""
        for (char in expression) {
            if (char.isDigit() || char == '.') {
                number += char
            } else if (char == 'x' || char == '*') {
                if (number.isNotEmpty()) output.add(number)
                number = ""
                while (operators.isNotEmpty() && (precedence[operators.peek()] ?: 0) >= (precedence['*'] ?: 0)) {
                    output.add(operators.pop().toString())
                }
                operators.push('*')
            } else {
                if (number.isNotEmpty()) {
                    output.add(number)
                    number = ""
                }
                while (operators.isNotEmpty() && (precedence[operators.peek()] ?: 0) >= (precedence[char] ?: 0)) {
                    output.add(operators.pop().toString())
                }
                operators.push(char)
            }
        }
        if (number.isNotEmpty()) output.add(number)
        while (operators.isNotEmpty()) output.add(operators.pop().toString())

        return output
    }

    fun evaluatePostfix(postfix: List<String>): String {
        val stack = Stack<Double>()
        for (token in postfix) {
            if (token.toDoubleOrNull() != null) {
                stack.push(token.toDouble())
            } else if (stack.size >= 2) {
                val num2 = stack.pop()
                val num1 = stack.pop()
                val result = when (token) {
                    "+" -> num1 + num2
                    "-" -> num1 - num2
                    "*" -> num1 * num2
                    "/" -> if (num2 != 0.0) num1 / num2 else return "ERR"
                    else -> return "ERR"
                }
                stack.push(result)
            } else return "ERR"
        }
        return if (stack.size == 1) stack.pop().toString() else "ERR"
    }
}