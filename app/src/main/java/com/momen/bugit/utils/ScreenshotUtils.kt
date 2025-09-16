package com.momen.bugit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ScreenshotUtils {
    
    fun captureView(view: View): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            android.util.Log.e("ScreenshotUtils", "Error capturing view: ${e.message}")
            null
        }
    }
    
    fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "bugit_screenshot_$timestamp.png"
            
            val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BugIt")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            
            val imageFile = File(storageDir, filename)
            val outputStream = FileOutputStream(imageFile)
            
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            
            android.util.Log.d("ScreenshotUtils", "Screenshot saved to: ${imageFile.absolutePath}")
            imageFile.absolutePath
        } catch (e: IOException) {
            android.util.Log.e("ScreenshotUtils", "Error saving bitmap: ${e.message}")
            null
        }
    }
    
    fun captureAndSaveView(context: Context, view: View): String? {
        val bitmap = captureView(view)
        return if (bitmap != null) {
            saveBitmapToFile(context, bitmap)
        } else {
            null
        }
    }
}
