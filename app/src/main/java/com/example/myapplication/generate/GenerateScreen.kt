package com.example.myapplication.generate

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.llm.FileProviderGeneratedImageStore
import com.example.myapplication.data.llm.OnDeviceImageGenerator
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private class GenerateViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = GenerateViewModel(
        imageGenerator = OnDeviceImageGenerator(File(appContext.filesDir, "models")),
        imageStore = FileProviderGeneratedImageStore(appContext)
    ) as T
}

@Composable
fun GenerateScreen(
    onBack: () -> Unit,
    viewModel: GenerateViewModel = viewModel(factory = GenerateViewModelFactory(LocalContext.current))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    GenerateContent(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@Composable
private fun GenerateContent(
    state: GenerateUiState,
    onIntent: (GenerateIntent) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "←",
                fontSize = 24.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 16.dp)
            )
            Text(
                text = "Generate Image",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))

        if (state.uploadSuccess) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Image uploaded!", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { onIntent(GenerateIntent.StartOver) }) { Text("Upload More") }
            }
            return@Column
        }

        OutlinedTextField(
            value = state.prompt,
            onValueChange = { onIntent(GenerateIntent.PromptChanged(it)) },
            label = { Text("Prompt") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { onIntent(GenerateIntent.Generate) },
            enabled = state.prompt.isNotBlank() && !state.isGenerating,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.size(8.dp))
                Text("Generating…")
            } else {
                Text("Generate")
            }
        }

        state.errorMessage?.let { message ->
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { onIntent(GenerateIntent.DismissError) }) { Text("Dismiss") }
            }
        }

        state.generatedImageUri?.let { uri ->
            Spacer(Modifier.height(16.dp))
            GeneratedPreview(
                uri = uri,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { onIntent(GenerateIntent.UploadImage) },
                enabled = !state.isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isUploading) "Uploading…" else "Upload")
            }
        }
    }
}

@Composable
private fun GeneratedPreview(uri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uri))?.use {
                    BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val image = bitmap
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = "Generated image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenerateEmptyPreview() {
    MyApplicationTheme { GenerateContent(GenerateUiState(), {}, {}) }
}

@Preview(showBackground = true)
@Composable
private fun GenerateGeneratingPreview() {
    MyApplicationTheme { GenerateContent(GenerateUiState(prompt = "A red fox", isGenerating = true), {}, {}) }
}

@Preview(showBackground = true)
@Composable
private fun GenerateErrorPreview() {
    MyApplicationTheme {
        GenerateContent(GenerateUiState(prompt = "A red fox", errorMessage = "Model not installed"), {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun GenerateSuccessPreview() {
    MyApplicationTheme { GenerateContent(GenerateUiState(uploadSuccess = true), {}, {}) }
}
