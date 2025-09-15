package com.momen.bugit.utils

import com.momen.bugit.data.model.BugFormState

object ValidationUtils {
    
    fun validateBugForm(formState: BugFormState): Boolean {
        return formState.title.isNotBlank() && 
               formState.description.isNotBlank() && 
               (formState.imagePath.isNotBlank() || formState.imageUrl.isNotBlank())
    }
}
