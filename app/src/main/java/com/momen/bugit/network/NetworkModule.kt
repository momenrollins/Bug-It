package com.momen.bugit.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    private const val GOOGLE_SHEETS_BASE_URL = "https://sheets.googleapis.com/"
    private const val IMAGEBB_BASE_URL = "https://api.imgbb.com/"
    
    private val gson: Gson by lazy {
        GsonBuilder()
            .create()
    }
    
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    val googleSheetsApiService: GoogleSheetsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GOOGLE_SHEETS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GoogleSheetsApiService::class.java)
    }
    
    val imageUploadApiService: ImageUploadApiService by lazy {
        Retrofit.Builder()
            .baseUrl(IMAGEBB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ImageUploadApiService::class.java)
    }
}
