package com.momen.bugit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.momen.bugit.ui.components.ImageSelector
import com.momen.bugit.ui.viewmodel.BugFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugFormScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: BugFormViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearError()
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
                onCameraClick = {
                    // TODO: Implement camera capture
                },
                onGalleryClick = {
                    // TODO: Implement gallery selection
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
            onClick = { viewModel.validateAndSubmit(onNavigateToSuccess) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !formState.isSubmitting
        ) {
            if (formState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submitting...")
            } else {
                Text("Submit Bug Report")
            }
        }
    }
}