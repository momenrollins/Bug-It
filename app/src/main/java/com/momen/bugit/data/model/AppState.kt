package com.momen.bugit.data.model

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class BugFormState(
    val title: String = "",
    val description: String = "",
    val imagePath: String = "",
    val imageUrl: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String = ""
)
