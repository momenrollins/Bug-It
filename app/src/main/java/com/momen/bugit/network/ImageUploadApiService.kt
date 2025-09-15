package com.momen.bugit.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

data class ImageBBResponse(
    val data: ImageBBData?,
    val success: Boolean,
    val status: Int
)

data class ImageBBData(
    val url: String,
    val display_url: String
)

interface ImageUploadApiService {
    @FormUrlEncoded
    @POST("1/upload")
    suspend fun uploadImage(
        @Field("key") apiKey: String,
        @Field("image") imageData: String
    ): Response<ImageBBResponse>
}
