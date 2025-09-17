package com.momen.bugit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.createBitmap

object ScreenshotUtils {
    
    fun captureAndSaveView(context: Context, view: View): String? {
        return try {
            // Create bitmap from view
            val bitmap = createBitmap(view.width, view.height)
            
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            
            // Save to file
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "screenshot_$timestamp.png"
            val file = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), filename)
            
            // Create directory if it doesn't exist
            file.parentFile?.mkdirs()
            
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            
            file.absolutePath
        } catch (e: Exception) {
            android.util.Log.e("ScreenshotUtils", "Error capturing screenshot: ${e.message}")
            null
        }
    }
}
