package com.momen.bugit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.momen.bugit.navigation.BugItNavigation
import com.momen.bugit.ui.theme.BugItTheme

class MainActivity : ComponentActivity() {
    private var currentIntent by mutableStateOf<Intent?>(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        currentIntent = intent
        setContent {
            BugItTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BugItApp(
                        modifier = Modifier.padding(innerPadding),
                        intent = currentIntent,
                        context = this@MainActivity
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        currentIntent = intent
    }
}

@Composable
fun BugItApp(modifier: Modifier = Modifier, intent: Intent? = null, context: Context) {
    val navController = rememberNavController()
    
    // Handle incoming image from other apps
    LaunchedEffect(intent) {
        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            android.util.Log.d("BugIt", "Received intent with image: $imageUri")
            if (imageUri != null) {
                try {
                    // Copy the image to our app's internal storage
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    if (inputStream != null) {
                        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
                        val filename = "shared_image_$timestamp.jpg"
                        val file = java.io.File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), filename)
                        
                        // Create directory if it doesn't exist
                        file.parentFile?.mkdirs()
                        
                        val outputStream = java.io.FileOutputStream(file)
                        inputStream.copyTo(outputStream)
                        inputStream.close()
                        outputStream.close()
                        
                        val localImagePath = file.absolutePath
                        android.util.Log.d("BugIt", "Copied image to: $localImagePath")
                        
                        // Navigate to bug form with the local file path
                        val route = "${com.momen.bugit.navigation.Screen.BugForm.route}?imageUri=$localImagePath"
                        android.util.Log.d("BugIt", "Navigating to: $route")
                        navController.navigate(route)
                    } else {
                        android.util.Log.e("BugIt", "Could not open input stream for URI: $imageUri")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("BugIt", "Error copying image: ${e.message}")
                    // Fallback to original URI
                    val route = "${com.momen.bugit.navigation.Screen.BugForm.route}?imageUri=${imageUri.toString()}"
                    navController.navigate(route)
                }
            }
        }
    }
    
    BugItNavigation(navController = navController, modifier = modifier)
}