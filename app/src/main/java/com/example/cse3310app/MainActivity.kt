package com.example.cse3310app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cse3310app.data.repository.AppContainer
import com.example.cse3310app.navigation.AppNavGraph
import com.example.cse3310app.ui.theme.CSE3310APPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = AppContainer(applicationContext)
        setContent {
            CSE3310APPTheme {
                AppNavGraph(container)
            }
        }
    }
}