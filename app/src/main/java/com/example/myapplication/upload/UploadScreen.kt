package com.example.myapplication.upload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun UploadScreen(viewModel: UploadViewModel = viewModel(), onBack: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    UploadContent(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@Composable
private fun UploadContent(
    state: UploadUiState,
    onIntent: (UploadIntent) -> Unit,
    onBack: () -> Unit
) {
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            onIntent(UploadIntent.PhotosSelected(uris.map { it.toString() }))
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Text(
                    text = "Upload Photos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.uploadSuccess -> UploadSuccessContent(onStartOver = { onIntent(UploadIntent.StartOver) })
                state.selectedPhotos.isEmpty() -> EmptyState(onSelectClick = { pickerLauncher.launch(pickerRequest) })
                else -> PhotoGridState(
                    state = state,
                    onAddMore = { pickerLauncher.launch(pickerRequest) },
                    onIntent = onIntent
                )
            }
        }
    }
}

private val pickerRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

@Composable
private fun EmptyState(onSelectClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onSelectClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "+", fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Select Photos", fontSize = 16.sp)
    }
}

@Composable
private fun PhotoGridState(
    state: UploadUiState,
    onAddMore: () -> Unit,
    onIntent: (UploadIntent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Add more",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(16.dp)
                .clickable { onAddMore() }
        )

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onIntent(UploadIntent.DismissError) }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.selectedPhotos) { uriString ->
                PhotoThumbnail(
                    uriString = uriString,
                    onRemove = { onIntent(UploadIntent.RemovePhoto(uriString)) }
                )
            }
        }

        Button(
            onClick = { onIntent(UploadIntent.UploadPhotos) },
            enabled = state.selectedPhotos.isNotEmpty() && !state.isUploading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            if (state.isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Text(text = "  Uploading...")
            } else {
                Text(text = "Upload (${state.selectedPhotos.size})")
            }
        }
    }
}

private const val THUMBNAIL_TARGET_PX = 200

private fun decodeSampledBitmap(
    context: Context,
    uri: Uri,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, boundsOptions)
    } ?: return null

    val options = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(boundsOptions.outWidth, boundsOptions.outHeight, reqWidth, reqHeight)
        inJustDecodeBounds = false
    }
    return context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, options)
    }
}

private fun calculateInSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
    var inSampleSize = 1
    if (width > reqWidth || height > reqHeight) {
        val halfWidth = width / 2
        val halfHeight = height / 2
        while (halfWidth / inSampleSize >= reqWidth && halfHeight / inSampleSize >= reqHeight) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

@Composable
private fun PhotoThumbnail(uriString: String, onRemove: () -> Unit) {
    val context = LocalContext.current
    var bitmap by remember(uriString) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uriString) {
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                decodeSampledBitmap(context, Uri.parse(uriString), THUMBNAIL_TARGET_PX, THUMBNAIL_TARGET_PX)
                    ?.asImageBitmap()
            }.getOrNull()
        }
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
    ) {
        val currentBitmap = bitmap
        if (currentBitmap != null) {
            Image(
                bitmap = currentBitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp),
                strokeWidth = 2.dp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✕", color = Color.White, fontSize = 11.sp)
        }
    }
}

@Composable
private fun UploadSuccessContent(onStartOver: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "✓ Upload complete", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onStartOver) {
            Text(text = "Upload More")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadScreenEmptyPreview() {
    MyApplicationTheme {
        UploadContent(state = UploadUiState(), onIntent = {}, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadScreenWithPhotosPreview() {
    MyApplicationTheme {
        UploadContent(
            state = UploadUiState(selectedPhotos = listOf("content://fake/1", "content://fake/2")),
            onIntent = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadScreenSuccessPreview() {
    MyApplicationTheme {
        UploadContent(
            state = UploadUiState(selectedPhotos = listOf("content://fake/1"), uploadSuccess = true),
            onIntent = {},
            onBack = {}
        )
    }
}
