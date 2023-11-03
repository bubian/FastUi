package com.pds.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class KComponentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloContent()
        }
    }


    @Composable
    fun HelloContent() {
        Log.e("HelloContent", "1111")
        Column(modifier = Modifier.padding(16f.dp)) {
            // val (name, setName) = remember { mutableStateOf("start") } // 用var将 不起作用
            var name by remember { mutableStateOf("start") }
            // val name = remember { mutableStateOf("start") }
            Log.e("HelloContent", name)
            Text(
                text = name,
                modifier = Modifier.padding(bottom = 8f.dp),
                style = MaterialTheme.typography.h5
            )
            OutlinedTextField(
                value = name,
                // onValueChange = { setName.invoke(it) },
                onValueChange = { name = it },
                label = { Text("Name") }
            )
        }
    }
}

@Composable
fun Greeting(name: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = { /* do something */ },
        interactionSource = interactionSource
    ) {
        Text(if (isPressed) "Pressed!" else "Not pressed")
    }
}