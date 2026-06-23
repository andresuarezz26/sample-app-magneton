package com.example.myapplication.upload

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.PaperStatus

@Composable
fun ProcessingScreen(
    paperId: String,
    onCompleted: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProcessingViewModel = viewModel(factory = ProcessingViewModelFactory(paperId))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state.status == PaperStatus.COMPLETED) {
            onCompleted()
        }
    }

    ProcessingContent(state = state, onBack = onBack)
}

@Composable
private fun ProcessingContent(state: ProcessingUiState, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .testTag("processingScreen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedPlaceholder()

        Text(
            text = "Processing your paper...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp)
        )

        Text(
            text = "Status: ${state.status.name}",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag("processingMessage")
        )

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun AnimatedPlaceholder() {
    val infiniteTransition = rememberInfiniteTransition(label = "placeholder_animation")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(
                color = Color.LightGray.copy(alpha = alpha),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = Color.Gray
        )
    }
}
