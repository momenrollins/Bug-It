package com.momen.bugit.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BugItApiService {
    @FormUrlEncoded
    @POST("1/upload")
    suspend fun uploadImage(
        @Field("key") apiKey: String,
        @Field("image") imageData: String
    ): Response<ImageBBResponse>

    @POST("v4/spreadsheets/{spreadsheetId}/values/{range}:append")
    suspend fun appendValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("key") apiKey: String,
        @Body request: AppendRequest
    ): Response<AppendResponse>
}
