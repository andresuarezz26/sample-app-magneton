package com.example.myapplication.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UploadScreen(
    viewModel: UploadViewModel = viewModel(),
    onPaperUploaded: (paperId: String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.paperId != null) {
        onPaperUploaded(state.paperId!!)
        return
    }

    UploadContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack
    )
}

@Composable
private fun UploadContent(
    state: UploadUiState,
    onIntent: (UploadIntent) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Back")
        }

        TabRow(
            selectedTabIndex = state.selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = state.selectedTab == 0,
                onClick = { onIntent(UploadIntent.SelectTab(0)) },
                text = { Text("File") }
            )
            Tab(
                selected = state.selectedTab == 1,
                onClick = { onIntent(UploadIntent.SelectTab(1)) },
                text = { Text("DOI/URL") },
                modifier = Modifier.testTag("tab_doi")
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state.selectedTab) {
                0 -> FileTab(state, onIntent)
                1 -> DoiTab(state, onIntent)
            }
        }
    }
}

@Composable
private fun FileTab(state: UploadUiState, onIntent: (UploadIntent) -> Unit) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onIntent(UploadIntent.PickPdf(uri))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { filePickerLauncher.launch("application/pdf") },
            modifier = Modifier.testTag("btn_select_pdf")
        ) {
            Text("Select PDF")
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        if (state.error != null) {
            Text(
                text = state.error!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(onClick = { onIntent(UploadIntent.Retry) }) {
                Text("Retry")
            }
        }

        Button(
            onClick = { onIntent(UploadIntent.SubmitFile) },
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("btn_submit"),
            enabled = !state.isLoading
        ) {
            Text("Upload")
        }
    }
}

@Composable
private fun DoiTab(state: UploadUiState, onIntent: (UploadIntent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = state.doiInput,
            onValueChange = { onIntent(UploadIntent.EnterDoi(it)) },
            label = { Text("Enter DOI or arXiv URL") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_doi")
        )

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        if (state.error != null) {
            Text(
                text = state.error!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(onClick = { onIntent(UploadIntent.Retry) }) {
                Text("Retry")
            }
        }

        Button(
            onClick = { onIntent(UploadIntent.SubmitDoi) },
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("btn_submit"),
            enabled = !state.isLoading
        ) {
            Text("Upload")
        }
    }
}
