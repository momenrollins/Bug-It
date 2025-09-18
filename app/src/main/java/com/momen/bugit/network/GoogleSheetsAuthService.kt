package com.momen.bugit.network

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.auth.http.HttpCredentialsAdapter
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.api.services.sheets.v4.model.AddSheetRequest
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.SheetProperties
import android.content.Context
import android.util.Log
import com.momen.bugit.R
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleSheetsAuthService(private val context: Context) {

    private fun getSheetsService(): Sheets? {
        return try {
            Log.d("GoogleSheetsAuth", "Creating Sheets service...")

            // Read the service account JSON from raw resources
            val resourceId = R.raw.service_account

            val inputStream: InputStream = context.resources.openRawResource(resourceId)
            Log.d("GoogleSheetsAuth", "Service account JSON loaded successfully")

            val credentials = ServiceAccountCredentials.fromStream(inputStream)
                .createScoped(listOf(SheetsScopes.SPREADSHEETS))

            inputStream.close()
            Log.d("GoogleSheetsAuth", "Credentials created successfully")

            val sheetsService = Sheets.Builder(
                com.google.api.client.http.javanet.NetHttpTransport(),
                com.google.api.client.json.gson.GsonFactory(),
                HttpCredentialsAdapter(credentials)
            )
                .setApplicationName("BugIt")
                .build()

            Log.d("GoogleSheetsAuth", "Sheets service built successfully")
            sheetsService
        } catch (e: Exception) {
            Log.e("GoogleSheetsAuth", "Error creating Sheets service: ${e.message}", e)
            null
        }
    }

    private suspend fun ensureDailySheetExists(
        sheetsService: Sheets,
        spreadsheetId: String,
        sheetName: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("GoogleSheetsAuth", "Checking if sheet '$sheetName' exists...")

                // Get all sheets in the spreadsheet
                val spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute()
                val existingSheets = spreadsheet.sheets?.map { it.properties?.title } ?: emptyList()

                Log.d("GoogleSheetsAuth", "Existing sheets: $existingSheets")

                if (sheetName in existingSheets) {
                    Log.d("GoogleSheetsAuth", "Sheet '$sheetName' already exists")
                    return@withContext true
                }

                Log.d("GoogleSheetsAuth", "Creating new sheet '$sheetName'...")

                // Create a new sheet
                val addSheetRequest = AddSheetRequest().apply {
                    properties = SheetProperties().apply {
                        title = sheetName
                    }
                }

                val batchUpdateRequest = BatchUpdateSpreadsheetRequest().apply {
                    requests = listOf(
                        Request().apply {
                            addSheet = addSheetRequest
                        }
                    )
                }

                val response =
                    sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
                        .execute()

                if (response.replies?.isNotEmpty() == true) {
                    Log.d("GoogleSheetsAuth", "Successfully created sheet '$sheetName'")

                    // Add headers to the new sheet
                    val headerRange = "$sheetName!A1:D1"
                    val headerValues =
                        listOf(listOf("Timestamp", "Title", "Description", "Image URL"))

                    val headerValueRange = ValueRange().apply {
                        setValues(headerValues)
                        majorDimension = "ROWS"
                    }

                    val headerRequest = sheetsService.spreadsheets().values().update(
                        spreadsheetId,
                        headerRange,
                        headerValueRange
                    ).apply {
                        valueInputOption = "USER_ENTERED"
                    }

                    headerRequest.execute()
                    Log.d("GoogleSheetsAuth", "Added headers to sheet '$sheetName'")

                    return@withContext true
                } else {
                    Log.e("GoogleSheetsAuth", "Failed to create sheet '$sheetName'")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e("GoogleSheetsAuth", "Error creating sheet '$sheetName': ${e.message}", e)
                return@withContext false
            }
        }
    }

    suspend fun appendValues(
        spreadsheetId: String,
        range: String,
        values: List<List<String>>
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(
                    "GoogleSheetsAuth",
                    "Starting appendValues - spreadsheetId: $spreadsheetId, range: $range"
                )
                Log.d("GoogleSheetsAuth", "Values to append: $values")

                val sheetsService = getSheetsService() ?: return@withContext Result.failure(
                    Exception("Failed to create Sheets service")
                )
                Log.d("GoogleSheetsAuth", "Sheets service created successfully")

                // Extract sheet name from range (e.g., "16-09-25!A:D" -> "16-09-25")
                val sheetName = range.substringBefore("!")
                Log.d("GoogleSheetsAuth", "Extracted sheet name: $sheetName")

                // Ensure the daily sheet exists
                val sheetExists = ensureDailySheetExists(sheetsService, spreadsheetId, sheetName)
                if (!sheetExists) {
                    Log.e("GoogleSheetsAuth", "Failed to create or verify sheet '$sheetName'")
                    return@withContext Result.failure(Exception("Failed to create daily sheet '$sheetName'"))
                }

                val valueRange = ValueRange().apply {
                    setValues(values.map { it.map { value -> value } })
                    majorDimension = "ROWS"
                }
                Log.d("GoogleSheetsAuth", "ValueRange created: $valueRange")

                // Try the specific range first, then fallback to Sheet1
                val rangesToTry = listOf(range, "Sheet1!A:D")

                for (currentRange in rangesToTry) {
                    try {
                        Log.d("GoogleSheetsAuth", "Trying range: $currentRange")

                        val request = sheetsService.spreadsheets().values().append(
                            spreadsheetId,
                            currentRange,
                            valueRange
                        ).apply {
                            insertDataOption = "INSERT_ROWS"
                            valueInputOption = "USER_ENTERED"
                        }

                        Log.d("GoogleSheetsAuth", "Request created, executing...")

                        val response = request.execute()
                        Log.d("GoogleSheetsAuth", "Response received: $response")

                        if (response != null && response.updates != null) {
                            Log.d(
                                "GoogleSheetsAuth",
                                "Successfully appended ${response.updates.updatedRows} rows to range: $currentRange"
                            )
                            return@withContext Result.success(true)
                        } else {
                            Log.w(
                                "GoogleSheetsAuth",
                                "Response is null or updates is null for range: $currentRange. Response: $response"
                            )
                        }
                    } catch (e: Exception) {
                        Log.w(
                            "GoogleSheetsAuth",
                            "Failed to append to range $currentRange: ${e.message}"
                        )
                        if (currentRange == rangesToTry.last()) {
                            throw e // Re-throw if this was the last attempt
                        }
                    }
                }

                Result.failure(Exception("Failed to append values to any sheet range"))
            } catch (e: Exception) {
                Log.e("GoogleSheetsAuth", "Error appending values: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
}
