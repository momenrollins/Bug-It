package com.momen.bugit.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.US)
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    
    fun getCurrentDateString(): String {
        return dateFormat.format(Date())
    }
    
    fun getCurrentTimestamp(): String {
        return timestampFormat.format(Date())
    }
    
    fun formatDateForSheet(date: Date): String {
        return dateFormat.format(date)
    }
    
    fun formatTimestampForSheet(date: Date): String {
        return timestampFormat.format(date)
    }
}
