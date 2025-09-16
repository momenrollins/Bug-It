package com.momen.bugit.network

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.auth.http.HttpCredentialsAdapter
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import android.content.Context
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleSheetsAuthService(private val context: Context) {
    
    private fun getSheetsService(): Sheets? {
        return try {
            android.util.Log.d("GoogleSheetsAuth", "Creating Sheets service...")
            
            // Read the service account JSON from raw resources
            val resourceId = context.resources.getIdentifier("service_account", "raw", context.packageName)
            android.util.Log.d("GoogleSheetsAuth", "Resource ID: $resourceId")
            
            if (resourceId == 0) {
                android.util.Log.e("GoogleSheetsAuth", "service_account.json not found in raw resources")
                return null
            }
            
            val inputStream: InputStream = context.resources.openRawResource(resourceId)
            android.util.Log.d("GoogleSheetsAuth", "Service account JSON loaded successfully")
            
            val credentials = ServiceAccountCredentials.fromStream(inputStream)
                .createScoped(listOf(SheetsScopes.SPREADSHEETS))
            
            inputStream.close()
            android.util.Log.d("GoogleSheetsAuth", "Credentials created successfully")
            
            val sheetsService = Sheets.Builder(
                com.google.api.client.http.javanet.NetHttpTransport(),
                com.google.api.client.json.gson.GsonFactory(),
                HttpCredentialsAdapter(credentials)
            )
                .setApplicationName("BugIt")
                .build()
            
            android.util.Log.d("GoogleSheetsAuth", "Sheets service built successfully")
            sheetsService
        } catch (e: Exception) {
            android.util.Log.e("GoogleSheetsAuth", "Error creating Sheets service: ${e.message}", e)
            null
        }
    }
    
    suspend fun appendValues(
        spreadsheetId: String,
        range: String,
        values: List<List<String>>
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("GoogleSheetsAuth", "Starting appendValues - spreadsheetId: $spreadsheetId, range: $range")
                android.util.Log.d("GoogleSheetsAuth", "Values to append: $values")
                
                val sheetsService = getSheetsService() ?: return@withContext Result.failure(Exception("Failed to create Sheets service"))
                android.util.Log.d("GoogleSheetsAuth", "Sheets service created successfully")
                
                val valueRange = ValueRange().apply {
                    setValues(values.map { it.map { value -> value } })
                    majorDimension = "ROWS"
                }
                android.util.Log.d("GoogleSheetsAuth", "ValueRange created: $valueRange")
                
                // Try the specific range first, then fallback to Sheet1
                val rangesToTry = listOf(range, "Sheet1!A:D")
                
                for (currentRange in rangesToTry) {
                    try {
                        android.util.Log.d("GoogleSheetsAuth", "Trying range: $currentRange")
                        
                        val request = sheetsService.spreadsheets().values().append(
                            spreadsheetId,
                            currentRange,
                            valueRange
                        ).apply {
                            insertDataOption = "INSERT_ROWS"
                            valueInputOption = "USER_ENTERED"
                        }
                        
                        android.util.Log.d("GoogleSheetsAuth", "Request created, executing...")
                        
                        val response = request.execute()
                        android.util.Log.d("GoogleSheetsAuth", "Response received: $response")
                        
                        if (response != null && response.updates != null) {
                            android.util.Log.d("GoogleSheetsAuth", "Successfully appended ${response.updates.updatedRows} rows to range: $currentRange")
                            return@withContext Result.success(true)
                        } else {
                            android.util.Log.w("GoogleSheetsAuth", "Response is null or updates is null for range: $currentRange. Response: $response")
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("GoogleSheetsAuth", "Failed to append to range $currentRange: ${e.message}")
                        if (currentRange == rangesToTry.last()) {
                            throw e // Re-throw if this was the last attempt
                        }
                    }
                }
                
                Result.failure(Exception("Failed to append values to any sheet range"))
            } catch (e: Exception) {
                android.util.Log.e("GoogleSheetsAuth", "Error appending values: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
}
