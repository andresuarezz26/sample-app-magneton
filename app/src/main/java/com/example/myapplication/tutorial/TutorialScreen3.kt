package com.example.myapplication.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

private const val TOTAL_TUTORIAL_PAGES = 4
private const val CURRENT_TUTORIAL_PAGE = 2

@Composable
fun TutorialScreen3(
    onNext: () -> Unit,
    onSkip: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.End)
                .testTag("btnSkipTutorial"),
        ) {
            Text("Skip")
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            )
            Text(
                text = "Like what you see",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("tutorialHeadline"),
            )
            Text(
                text = "Double-tap or hit the heart on any video to like it, and swipe up to jump to the next one in your feed.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("tutorialBody"),
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            PageIndicator(
                pageCount = TOTAL_TUTORIAL_PAGES,
                currentPage = CURRENT_TUTORIAL_PAGE,
            )
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btnNextTutorial"),
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
private fun PageIndicator(pageCount: Int, currentPage: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pageCount) { page ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .then(
                        Modifier.background(
                            if (page == currentPage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TutorialScreen3Preview() {
    MyApplicationTheme {
        TutorialScreen3(onNext = {}, onSkip = {})
    }
}
