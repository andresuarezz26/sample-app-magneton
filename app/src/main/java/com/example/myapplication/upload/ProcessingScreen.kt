package com.example.myapplication.upload

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.PaperStatus
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * Shows an animated placeholder + message while the backend processes the paper, and a
 * completion state with a "Done" action once the polled status reaches
 * [PaperStatus.COMPLETED]. Processing typically takes 30-120s.
 */
@Composable
fun ProcessingScreen(
    state: UploadUiState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isComplete = state.status == PaperStatus.COMPLETED

    Column(
        modifier = modifier
            .testTag(UploadTestTags.PROCESSING_SCREEN)
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isComplete) {
            CompletedPlaceholder()
            Text(
                text = "Your video is ready!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag(UploadTestTags.PROCESSING_MESSAGE)
            )
            Text(
                text = "It's now available in your profile under \"My Papers\".",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = onDone,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text("Done")
            }
        } else {
            PulsingPlaceholder()
            Text(
                text = "Processing your paper…",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag(UploadTestTags.PROCESSING_MESSAGE)
            )
            Text(
                text = "We're extracting key findings and generating your narrated video. " +
                    "This usually takes 30-120 seconds.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
        }
    }
}

@Composable
private fun PulsingPlaceholder() {
    val transition = rememberInfiniteTransition(label = "processing-pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "processing-pulse-alpha"
    )
    Box(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .size(120.dp)
            .clip(CircleShape)
            .alpha(alpha)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🎬", fontSize = 48.sp)
    }
}

@Composable
private fun CompletedPlaceholder() {
    Box(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "✅", fontSize = 48.sp)
    }
}

@Preview(showBackground = true)
@Composable
private fun ProcessingScreenPreview() {
    MyApplicationTheme {
        ProcessingScreen(
            state = UploadUiState(phase = UploadPhase.PROCESSING, status = PaperStatus.PROCESSING),
            onDone = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProcessingScreenCompletePreview() {
    MyApplicationTheme {
        ProcessingScreen(
            state = UploadUiState(phase = UploadPhase.COMPLETED, status = PaperStatus.COMPLETED),
            onDone = {}
        )
    }
}
