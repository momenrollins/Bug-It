package com.momen.bugit.data.model

data class GoogleSheetsConfig(
    val spreadsheetId: String,
    val credentialsPath: String = ""
)

data class SheetRow(
    val timestamp: String,
    val title: String,
    val description: String,
    val imageUrl: String
)
