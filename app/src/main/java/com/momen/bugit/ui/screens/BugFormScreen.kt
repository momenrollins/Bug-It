package com.momen.bugit.ui.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.momen.bugit.ui.components.ImageSelector
import com.momen.bugit.ui.viewmodel.BugFormViewModel
import com.momen.bugit.data.model.SubmissionStep

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BugFormScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    initialImageUri: String? = null,
    viewModel: BugFormViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    val permissionState = rememberPermissionState(permission)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            viewModel.updateImagePath(selectedUri.toString())
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    // Handle initial image URI from other apps
    LaunchedEffect(initialImageUri) {
        Log.d("BugFormScreen", "Initial image URI: $initialImageUri")
        initialImageUri?.let { uri ->
            Log.d("BugFormScreen", "Setting image path: $uri")
            viewModel.updateImagePath(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Report Bug",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = formState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Bug Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.errorMessage.isNotBlank() && formState.title.isBlank(),
                supportingText = if (formState.errorMessage.isNotBlank() && formState.title.isBlank()) {
                    { Text(formState.errorMessage) }
                } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = formState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                isError = formState.errorMessage.isNotBlank() && formState.description.isBlank(),
                supportingText = if (formState.errorMessage.isNotBlank() && formState.description.isBlank()) {
                    { Text(formState.errorMessage) }
                } else null
            )

            Spacer(modifier = Modifier.height(24.dp))

            ImageSelector(
                imagePath = formState.imagePath,
                imageUrl = formState.imageUrl,
                onGalleryClick = {
                    if (permissionState.status.isGranted) {
                        galleryLauncher.launch("image/*")
                    } else {
                        permissionState.launchPermissionRequest()
                    }
                },
                onScreenshotClick = {
                    try {
                        val activity = context as? androidx.activity.ComponentActivity
                        if (activity != null) {
                            val rootView = activity.findViewById<android.view.View>(android.R.id.content)
                            val screenshotPath = com.momen.bugit.utils.ScreenshotUtils.captureAndSaveView(context, rootView)
                            if (screenshotPath != null) {
                                viewModel.updateScreenshotPath(screenshotPath)
                                Log.d("BugFormScreen", "Screenshot captured: $screenshotPath")
                            } else {
                                viewModel.setScreenshotError("Failed to capture screenshot")
                            }
                        } else {
                            viewModel.setScreenshotError("Unable to access activity for screenshot")
                        }
                    } catch (e: Exception) {
                        Log.e("BugFormScreen", "Error capturing screenshot: ${e.message}")
                        viewModel.setScreenshotError("Error capturing screenshot: ${e.message}")
                    }
                }
            )

            if (formState.errorMessage.isNotBlank() && formState.imagePath.isBlank() && formState.imageUrl.isBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.validateAndSubmit(context, onNavigateToSuccess) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !formState.isSubmitting
        ) {
            if (formState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    when (formState.submissionStep) {
                        SubmissionStep.UploadingImage -> "Uploading Image..."
                        SubmissionStep.SubmittingToSheets -> "Submitting to Google Sheets..."
                        else -> "Submitting..."
                    }
                )
            } else {
                Text("Submit Bug Report")
            }
        }
    }
}