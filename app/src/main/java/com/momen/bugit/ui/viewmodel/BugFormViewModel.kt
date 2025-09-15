package com.momen.bugit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momen.bugit.data.model.BugFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BugFormViewModel : ViewModel() {
    private val _formState = MutableStateFlow(BugFormState())
    val formState: StateFlow<BugFormState> = _formState.asStateFlow()

    fun updateTitle(title: String) {
        _formState.value = _formState.value.copy(
            title = title,
            errorMessage = ""
        )
    }

    fun updateDescription(description: String) {
        _formState.value = _formState.value.copy(
            description = description,
            errorMessage = ""
        )
    }

    fun updateImagePath(imagePath: String) {
        _formState.value = _formState.value.copy(
            imagePath = imagePath,
            errorMessage = ""
        )
    }

    fun updateImageUrl(imageUrl: String) {
        _formState.value = _formState.value.copy(
            imageUrl = imageUrl,
            errorMessage = ""
        )
    }

    fun validateAndSubmit(onSuccess: () -> Unit) {
        val currentState = _formState.value

        if (currentState.title.isBlank()) {
            _formState.value = currentState.copy(errorMessage = "Title is required")
            return
        }

        if (currentState.description.isBlank()) {
            _formState.value = currentState.copy(errorMessage = "Description is required")
            return
        }

        if (currentState.imagePath.isBlank() && currentState.imageUrl.isBlank()) {
            _formState.value = currentState.copy(errorMessage = "Image is required")
            return
        }

        viewModelScope.launch {
            _formState.value = currentState.copy(isSubmitting = true)

            try {
                // TODO: Implement actual submission logic
                kotlinx.coroutines.delay(2000) // Simulate network call
                onSuccess()
            } catch (e: Exception) {
                _formState.value = currentState.copy(
                    isSubmitting = false,
                    errorMessage = "Submission failed: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _formState.value = _formState.value.copy(errorMessage = "")
    }
}
