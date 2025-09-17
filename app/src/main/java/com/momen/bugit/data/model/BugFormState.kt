package com.momen.bugit.data.model

data class BugFormState(
    val title: String = "",
    val description: String = "",
    val imagePath: String = "",
    val imageUrl: String = "",
    val isSubmitting: Boolean = false,
    val submissionStep: SubmissionStep = SubmissionStep.Idle,
    val errorMessage: String = ""
)
