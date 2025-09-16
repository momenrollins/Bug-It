package com.momen.bugit.network

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun getTodayString(): String {
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.US)
        return formatter.format(Date())
    }
    
    fun getCurrentTimestamp(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        return formatter.format(Date())
    }
}