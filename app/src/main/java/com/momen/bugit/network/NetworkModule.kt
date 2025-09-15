package com.momen.bugit.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.momen.bugit.config.ApiConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    private const val GOOGLE_SHEETS_BASE_URL = ApiConfig.GOOGLE_SHEETS_BASE_URL
    private const val IMAGEBB_BASE_URL = ApiConfig.IMAGEBB_BASE_URL
    
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
    
    val imageUploadService: BugItApiService by lazy {
        Retrofit.Builder()
            .baseUrl(IMAGEBB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BugItApiService::class.java)
    }
    
    val sheetsService: BugItApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GOOGLE_SHEETS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BugItApiService::class.java)
    }
}
