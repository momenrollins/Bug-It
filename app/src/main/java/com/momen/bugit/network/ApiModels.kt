package com.momen.bugit.network

data class ImageBBResponse(
    val data: ImageBBData?,
    val success: Boolean,
    val status: Int
)

data class ImageBBData(
    val url: String,
    val display_url: String
)

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
