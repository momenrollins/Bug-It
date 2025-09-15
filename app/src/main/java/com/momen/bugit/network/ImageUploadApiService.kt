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
    val id: String,
    val title: String,
    val url_viewer: String,
    val url: String,
    val display_url: String,
    val width: String,
    val height: String,
    val size: String,
    val time: String,
    val expiration: String,
    val image: ImageBBImage,
    val thumb: ImageBBThumb,
    val delete_url: String
)

data class ImageBBImage(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)

data class ImageBBThumb(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)

interface ImageUploadApiService {
    @FormUrlEncoded
    @POST("1/upload")
    suspend fun uploadImage(
        @Field("key") apiKey: String,
        @Field("image") imageData: String
    ): Response<ImageBBResponse>
}
