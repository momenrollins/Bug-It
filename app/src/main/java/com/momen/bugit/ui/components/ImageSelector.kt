package com.momen.bugit.ui.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

@Composable
fun ImageSelector(
    imagePath: String,
    imageUrl: String,
    onGalleryClick: () -> Unit,
    onScreenshotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasImage = imagePath.isNotBlank() || imageUrl.isNotBlank()
    val context = LocalContext.current

    Log.d("ImageSelector", "hasImage: $hasImage, imagePath: '$imagePath', imageUrl: '$imageUrl'")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = if (hasImage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (hasImage)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        if (hasImage) {
            val imageModel = if (imagePath.isNotBlank()) {
                Log.d("BugIt", "ImageSelector - imagePath: $imagePath")
                if (imagePath.startsWith("file://")) {
                    // Handle file URIs
                    val filePath = imagePath.substring(7) // Remove "file://" prefix
                    Log.d("BugIt", "ImageSelector - file path: $filePath")
                    File(filePath)
                } else if (imagePath.startsWith("/")) {
                    // Handle absolute file paths (from copied images)
                    Log.d("BugIt", "ImageSelector - absolute file path: $imagePath")
                    File(imagePath)
                } else {
                    // Handle content URIs
                    Log.d("BugIt", "ImageSelector - using content URI directly")
                    try {
                        val uri = imagePath.toUri()
                        ImageRequest.Builder(context)
                            .data(uri)
                            .crossfade(true)
                            .build()
                    } catch (e: Exception) {
                        Log.e("BugIt", "Error creating ImageRequest: ${e.message}")
                        imagePath
                    }
                }
            } else {
                Log.d("BugIt", "ImageSelector - using imageUrl: $imageUrl")
                imageUrl
            }

            AsyncImage(
                model = imageModel,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                onError = {
                    Log.e("BugIt", "AsyncImage error: ${it.result.throwable.message}")
                },
                onSuccess = {
                    Log.d("BugIt", "AsyncImage loaded successfully")
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add Screenshot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onScreenshotClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📸 Screenshot")
                    }

                    OutlinedButton(
                        onClick = onGalleryClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🖼️ Gallery")
                    }
                }
            }
        }
    }
}
