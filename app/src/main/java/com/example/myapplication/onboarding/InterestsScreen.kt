package com.example.myapplication.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.ScientificField
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun InterestsScreen(
    state: OnboardingUiState,
    onToggleField: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag("interestsScreen")
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "What are you into?",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select at least one field to personalize your feed.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.fields, key = { it.id }) { field ->
                FilterChip(
                    selected = field.id in state.selectedFieldIds,
                    onClick = { onToggleField(field.id) },
                    label = { Text(field.name) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onContinue,
            enabled = state.canContinue,
            modifier = Modifier
                .testTag("btnContinue")
                .fillMaxWidth()
        ) {
            Text(text = "Continue")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InterestsScreenPreview() {
    MyApplicationTheme {
        InterestsScreen(
            state = OnboardingUiState(
                fields = listOf(
                    ScientificField("cs", "Computer Science"),
                    ScientificField("biology", "Biology"),
                    ScientificField("physics", "Physics")
                ),
                selectedFieldIds = setOf("cs")
            ),
            onToggleField = {},
            onContinue = {}
        )
    }
}
