package com.srizan.printy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.srizan.printy.feature.canvas.CanvasScreen
import com.srizan.printy.ui.theme.PosprinterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PosprinterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CanvasScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
