package com.example.myapplication.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.ScientificField
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun InterestSelectionScreen(
    state: OnboardingUiState,
    onToggleField: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("interestSelectionScreen")
    ) {
        Text(
            text = "What are you into?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(24.dp)
        )
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.fields, key = { it.id }) { field ->
                    val isSelected = field.id in state.selectedFieldIds
                    Card(
                        modifier = Modifier
                            .testTag("field_${field.id}")
                            .selectable(selected = isSelected, onClick = { onToggleField(field.id) }),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = field.name)
                        }
                    }
                }
            }
        }
        Button(
            onClick = onContinue,
            enabled = state.canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("btnContinue")
        ) {
            Text("Continue")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InterestSelectionScreenPreview() {
    MyApplicationTheme {
        InterestSelectionScreen(
            state = OnboardingUiState(
                fields = listOf(
                    ScientificField("physics", "Physics"),
                    ScientificField("biology", "Biology"),
                    ScientificField("computer_science", "Computer Science")
                ),
                selectedFieldIds = setOf("physics")
            ),
            onToggleField = {},
            onContinue = {}
        )
    }
}
