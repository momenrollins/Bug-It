package com.momen.bugit.network

data class BugData(
    val timestamp: String,
    val title: String,
    val description: String,
    val imageUrl: String
)

data class ImageUploadResult(
    val success: Boolean,
    val imageUrl: String = "",
    val errorMessage: String = ""
)
