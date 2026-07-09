package com.example.myapplication.upload

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.UploadSource
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * Compose test tags for the self-contained upload flow. These adapt the ticket's Espresso
 * View-ID assertions (fab_upload, tab_doi, input_doi, btn_submit, processingScreen,
 * processingMessage) to `onNodeWithTag`, since the app is Compose-only.
 */
object UploadTestTags {
    const val FAB_UPLOAD = "fab_upload"
    const val TAB_FILE = "tab_file"
    const val TAB_DOI = "tab_doi"
    const val INPUT_DOI = "input_doi"
    const val BTN_SUBMIT = "btn_submit"
    const val PROCESSING_SCREEN = "processingScreen"
    const val PROCESSING_MESSAGE = "processingMessage"
}

/**
 * Entry point for the upload flow. Owns the [UploadViewModel] and switches between the input
 * form and the [ProcessingScreen] purely from [UploadUiState.phase], since no navigation
 * library exists in this skeleton.
 */
@Composable
fun UploadScreen(
    onClose: () -> Unit,
    viewModel: UploadViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    UploadContent(state = state, onIntent = viewModel::onIntent, onClose = onClose)
}

@Composable
private fun UploadContent(
    state: UploadUiState,
    onIntent: (UploadIntent) -> Unit,
    onClose: () -> Unit
) {
    when (state.phase) {
        UploadPhase.PROCESSING, UploadPhase.COMPLETED ->
            ProcessingScreen(
                state = state,
                onDone = onClose,
                modifier = Modifier.statusBarsPadding()
            )
        else -> UploadForm(state = state, onIntent = onIntent, onClose = onClose)
    }
}

@Composable
private fun UploadForm(
    state: UploadUiState,
    onIntent: (UploadIntent) -> Unit,
    onClose: () -> Unit
) {
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onIntent(UploadIntent.SelectPdf(uri, uri.lastPathSegment))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Upload a paper", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onClose) { Text("Close") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val selectedTab = if (state.source == UploadSource.FILE) 0 else 1
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onIntent(UploadIntent.SelectSource(UploadSource.FILE)) },
                text = { Text("PDF file") },
                modifier = Modifier.testTag(UploadTestTags.TAB_FILE)
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onIntent(UploadIntent.SelectSource(UploadSource.DOI)) },
                text = { Text("DOI / URL") },
                modifier = Modifier.testTag(UploadTestTags.TAB_DOI)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (state.source) {
            UploadSource.FILE -> FilePicker(
                fileName = state.selectedPdfName,
                onPick = { pdfPicker.launch("application/pdf") }
            )
            UploadSource.DOI -> OutlinedTextField(
                value = state.doi,
                onValueChange = { onIntent(UploadIntent.EnterDoi(it)) },
                label = { Text("DOI or arXiv URL") },
                placeholder = { Text("10.1038/s41586-021-03819-2") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(UploadTestTags.INPUT_DOI)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isUploading) {
            Text(text = "Uploading…", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Button(
                onClick = { onIntent(UploadIntent.Submit) },
                enabled = state.canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(UploadTestTags.BTN_SUBMIT)
            ) {
                Text("Submit")
            }
        }

        val error = state.error
        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { onIntent(UploadIntent.Retry) }) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun FilePicker(fileName: String?, onPick: () -> Unit) {
    Column {
        OutlinedButton(
            onClick = onPick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (fileName == null) "Choose PDF" else "Change PDF")
        }
        if (fileName != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Selected: $fileName", fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadFormDoiPreview() {
    MyApplicationTheme {
        UploadForm(
            state = UploadUiState(source = UploadSource.DOI, doi = "10.1038/s41586-021-03819-2"),
            onIntent = {},
            onClose = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadFormErrorPreview() {
    MyApplicationTheme {
        UploadForm(
            state = UploadUiState(source = UploadSource.DOI, error = "Upload failed. Please try again."),
            onIntent = {},
            onClose = {}
        )
    }
}
