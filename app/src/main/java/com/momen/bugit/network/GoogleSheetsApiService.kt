package com.momen.bugit.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

data class AppendRequest(
    val values: List<List<String>>,
    val majorDimension: String = "ROWS",
    val insertDataOption: String = "INSERT_ROWS"
)

data class AppendResponse(
    val spreadsheetId: String,
    val tableRange: String,
    val updates: Updates
)

data class Updates(
    val spreadsheetId: String,
    val updatedRange: String,
    val updatedRows: Int,
    val updatedColumns: Int,
    val updatedCells: Int
)

interface GoogleSheetsApiService {
    @POST("v4/spreadsheets/{spreadsheetId}/values/{range}:append")
    suspend fun appendValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Body request: AppendRequest
    ): Response<AppendResponse>
}
