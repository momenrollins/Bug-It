package com.momen.bugit.ui.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageSelector(
    imagePath: String,
    imageUrl: String,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasImage = imagePath.isNotBlank() || imageUrl.isNotBlank()
    val context = LocalContext.current
    
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
                android.util.Log.d("BugIt", "ImageSelector - imagePath: $imagePath")
                if (imagePath.startsWith("file://")) {
                    // Handle file URIs
                    val filePath = imagePath.substring(7) // Remove "file://" prefix
                    android.util.Log.d("BugIt", "ImageSelector - file path: $filePath")
                    java.io.File(filePath)
                } else {
                    // Handle content URIs - try different approaches
                    android.util.Log.d("BugIt", "ImageSelector - using content URI directly")
                    try {
                        val uri = Uri.parse(imagePath)
                        ImageRequest.Builder(context)
                            .data(uri)
                            .crossfade(true)
                            .build()
                    } catch (e: Exception) {
                        android.util.Log.e("BugIt", "Error creating ImageRequest: ${e.message}")
                        imagePath
                    }
                }
            } else {
                android.util.Log.d("BugIt", "ImageSelector - using imageUrl: $imageUrl")
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
                    android.util.Log.e("BugIt", "AsyncImage error: ${it.result.throwable?.message}")
                },
                onSuccess = {
                    android.util.Log.d("BugIt", "AsyncImage loaded successfully")
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
                
                OutlinedButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🖼️ Select from Gallery")
                }
            }
        }
    }
}
