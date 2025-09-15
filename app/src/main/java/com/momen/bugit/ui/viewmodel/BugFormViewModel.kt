package com.momen.bugit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momen.bugit.data.model.BugFormState
import com.momen.bugit.data.model.SubmissionStep
import com.momen.bugit.repository.BugRepository
import com.momen.bugit.config.ApiConfig
import com.momen.bugit.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import android.content.Context

class BugFormViewModel : ViewModel() {
    private val _formState = MutableStateFlow(BugFormState())
    val formState: StateFlow<BugFormState> = _formState.asStateFlow()

    private fun getRepository(context: Context) = BugRepository(
        NetworkModule.imageUploadService,
        NetworkModule.sheetsService,
        context
    )

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

    fun validateAndSubmit(context: Context, onSuccess: () -> Unit) {
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
            _formState.value = currentState.copy(
                isSubmitting = true,
                submissionStep = SubmissionStep.UploadingImage,
                errorMessage = ""
            )

            try {
                var imageUrl = currentState.imageUrl
                val repository = getRepository(context)

                // Step 1: Upload image if needed
                android.util.Log.d("BugIt", "Current state - imagePath: '${currentState.imagePath}', imageUrl: '${currentState.imageUrl}'")
                
                if (currentState.imagePath.isNotBlank() && currentState.imageUrl.isBlank()) {
                    android.util.Log.d("BugIt", "Starting image upload...")
                    val uploadResult = if (currentState.imagePath.startsWith("content://")) {
                        android.util.Log.d("BugIt", "Handling content URI: ${currentState.imagePath}")
                        repository.uploadImageFromUri(currentState.imagePath, ApiConfig.IMAGEBB_API_KEY)
                    } else {
                        android.util.Log.d("BugIt", "Handling file path: ${currentState.imagePath}")
                        val imageFile = File(currentState.imagePath)
                        android.util.Log.d("BugIt", "Image file exists: ${imageFile.exists()}")
                        
                        if (imageFile.exists()) {
                            try {
                                val imageData = imageFile.readBytes()
                                android.util.Log.d("BugIt", "Image data size: ${imageData.size} bytes")
                                repository.uploadImage(imageData, ApiConfig.IMAGEBB_API_KEY)
                            } catch (e: Exception) {
                                android.util.Log.e("BugIt", "Error reading image file: ${e.message}")
                                Result.failure(e)
                            }
                        } else {
                            android.util.Log.e("BugIt", "Image file not found at: ${imageFile.absolutePath}")
                            Result.failure(Exception("Image file not found"))
                        }
                    }

                    if (uploadResult.isSuccess) {
                        imageUrl = uploadResult.getOrNull()?.imageUrl ?: ""
                        android.util.Log.d("BugIt", "Image upload successful: $imageUrl")
                        _formState.value = _formState.value.copy(
                            imageUrl = imageUrl,
                            submissionStep = SubmissionStep.SubmittingToSheets
                        )
                    } else {
                        android.util.Log.e("BugIt", "Image upload failed: ${uploadResult.exceptionOrNull()?.message}")
                        _formState.value = currentState.copy(
                            isSubmitting = false,
                            submissionStep = SubmissionStep.Error,
                            errorMessage = "Image upload failed: ${uploadResult.exceptionOrNull()?.message}"
                        )
                        return@launch
                    }
                } else {
                    android.util.Log.d("BugIt", "Skipping image upload - going directly to sheets submission")
                    // Skip image upload, go directly to sheets submission
                    _formState.value = _formState.value.copy(submissionStep = SubmissionStep.SubmittingToSheets)
                }

                // Step 2: Submit to Google Sheets
                android.util.Log.d("BugIt", "Submitting to Google Sheets...")
                val bugData = com.momen.bugit.repository.BugData(
                    timestamp = com.momen.bugit.utils.DateUtils.getCurrentTimestamp(),
                    title = currentState.title,
                    description = currentState.description,
                    imageUrl = imageUrl
                )
                
                val sheetsResult = repository.submitBugToSheets(
                    bugData = bugData,
                    spreadsheetId = ApiConfig.GOOGLE_SHEETS_SPREADSHEET_ID,
                    apiKey = ApiConfig.GOOGLE_SHEETS_API_KEY
                )
                
                if (sheetsResult.isSuccess) {
                    android.util.Log.d("BugIt", "Google Sheets submission successful")
                    _formState.value = _formState.value.copy(
                        isSubmitting = false,
                        submissionStep = SubmissionStep.Success
                    )
                    onSuccess()
                } else {
                    android.util.Log.e("BugIt", "Google Sheets submission failed: ${sheetsResult.exceptionOrNull()?.message}")
                    _formState.value = currentState.copy(
                        isSubmitting = false,
                        submissionStep = SubmissionStep.Error,
                        errorMessage = "Failed to submit to Google Sheets: ${sheetsResult.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("BugIt", "Submission failed: ${e.message}")
                _formState.value = currentState.copy(
                    isSubmitting = false,
                    submissionStep = SubmissionStep.Error,
                    errorMessage = "Submission failed: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _formState.value = _formState.value.copy(errorMessage = "")
    }
}
