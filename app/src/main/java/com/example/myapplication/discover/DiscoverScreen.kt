package com.example.myapplication.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Paper
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun DiscoverScreen(viewModel: DiscoverViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DiscoverContent(state = state, onIntent = viewModel::onIntent)
}

@Composable
private fun DiscoverContent(state: DiscoverUiState, onIntent: (DiscoverIntent) -> Unit) {
    when (state) {
        is DiscoverUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize().testTag("discoverLoading"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is DiscoverUiState.Error -> {
            ErrorView(message = state.message, onRetry = { onIntent(DiscoverIntent.Retry) })
        }
        is DiscoverUiState.Success -> {
            Column(modifier = Modifier.fillMaxSize()) {
                CategoryFilterRow(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onSelect = { onIntent(DiscoverIntent.SelectCategory(it)) }
                )
                if (state.papers.isEmpty()) {
                    EmptyView()
                } else {
                    PaperFeed(
                        state = state,
                        onLoadNextPage = { onIntent(DiscoverIntent.LoadNextPage) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String?,
    onSelect: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onSelect(null) },
            label = { Text("All") },
            modifier = Modifier.testTag("categoryChip_All")
        )
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onSelect(category) },
                label = { Text(category) },
                modifier = Modifier.testTag("categoryChip_$category")
            )
        }
    }
}

@Composable
private fun PaperFeed(state: DiscoverUiState.Success, onLoadNextPage: () -> Unit) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisible >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadNextPage()
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("discoverFeed"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.papers, key = { it.id }) { paper ->
            PaperCard(paper = paper)
        }
        if (state.isLoadingNextPage) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun PaperCard(paper: Paper) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("paperCard")
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(paper.backgroundColorHex))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = paper.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.testTag("paperTitle")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = paper.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(paper.field) },
                    modifier = Modifier.testTag("fieldTag")
                )
            }
        }
    }
}

@Composable
private fun EmptyView() {
    Box(
        modifier = Modifier.fillMaxSize().testTag("discoverEmpty"),
        contentAlignment = Alignment.Center
    ) {
        Text("No papers found")
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().testTag("errorView"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscoverScreenPreview() {
    MyApplicationTheme {
        DiscoverContent(
            state = DiscoverUiState.Success(
                papers = listOf(
                    Paper(
                        id = "1",
                        title = "CRISPR-Cas9: Precision Gene Editing in Human Embryos",
                        authors = listOf("J. Zhang", "F. Doudna"),
                        field = "Biology",
                        likes = 48200,
                        comments = 1203,
                        shares = 892,
                        backgroundColorHex = 0xFF1B4332
                    )
                ),
                categories = listOf("Biology", "Physics", "CS", "Chemistry", "Neuroscience")
            ),
            onIntent = {}
        )
    }
}
