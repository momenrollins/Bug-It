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
