package com.momen.bugit.network

import android.util.Base64
import android.content.Context
import java.io.InputStream
import androidx.core.net.toUri

class BugRepository(
    private val imageUploadService: BugItApiService,
    private val context: Context
) {
    private val googleSheetsAuthService = GoogleSheetsAuthService(context)
    
    suspend fun uploadImageFromUri(uriString: String, imageBBApiKey: String): Result<ImageUploadResult> {
        return try {
            val uri = uriString.toUri()
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            
            if (inputStream != null) {
                val imageData = inputStream.readBytes()
                inputStream.close()
                uploadImage(imageData, imageBBApiKey)
            } else {
                Result.failure(Exception("Could not open input stream for URI: $uriString"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImage(imageData: ByteArray, imageBBApiKey: String): Result<ImageUploadResult> {
        return try {
            val base64Image = Base64.encodeToString(imageData, Base64.DEFAULT)
            val response = imageUploadService.uploadImage(
                apiKey = imageBBApiKey,
                imageData = base64Image
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val imageUrl = response.body()?.data?.url ?: ""
                Result.success(ImageUploadResult(
                    success = true,
                    imageUrl = imageUrl
                ))
            } else {
                Result.failure(Exception("Failed to upload image: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun submitBugToSheets(
        bugData: BugData,
        spreadsheetId: String
    ): Result<Boolean> {
        return try {
            val today = DateUtils.getTodayString()
            val range = "$today!A:D"
            
            val values = listOf(
                listOf(
                    bugData.timestamp,
                    bugData.title,
                    bugData.description,
                    bugData.imageUrl
                )
            )
            
            // Use the service account authentication
            googleSheetsAuthService.appendValues(
                spreadsheetId = spreadsheetId,
                range = range,
                values = values
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}