package com.momen.bugit.data.model

data class ImageUploadResponse(
    val success: Boolean,
    val imageUrl: String = "",
    val errorMessage: String = ""
)

// Simple Imgur API response - only what we need
data class ImgurResponse(
    val data: ImgurData?,
    val success: Boolean
)

data class ImgurData(
    val link: String  // Only need the image URL
)
