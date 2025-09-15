package com.momen.bugit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.momen.bugit.navigation.BugItNavigation
import com.momen.bugit.ui.theme.BugItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BugItTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BugItApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BugItApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    BugItNavigation(navController = navController, modifier = modifier)
}