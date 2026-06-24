package com.example.myapplication.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (state) {
            is DiscoverUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Blue
                )
            }

            is DiscoverUiState.Success -> {
                DiscoverFeedContent(
                    papers = state.papers,
                    selectedCategory = state.selectedCategory,
                    hasMore = state.hasMore,
                    onIntent = onIntent
                )
            }

            is DiscoverUiState.Error -> {
                ErrorView(message = state.message)
            }
        }
    }
}

@Composable
private fun DiscoverFeedContent(
    papers: List<Paper>,
    selectedCategory: String,
    hasMore: Boolean,
    onIntent: (DiscoverIntent) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val categories = listOf("All", "Physics", "Biology", "Chemistry", "CS")

    val isNearEnd by remember {
        derivedStateOf {
            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != null && lastVisibleItem.index >= papers.size - 2
        }
    }

    LaunchedEffect(isNearEnd) {
        if (isNearEnd && hasMore) {
            onIntent(DiscoverIntent.LoadNextPage)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                AssistChip(
                    onClick = {
                        onIntent(DiscoverIntent.SelectCategory(category))
                    },
                    label = { Text(category) },
                    modifier = Modifier.toggleable(
                        value = category == selectedCategory,
                        onValueChange = {
                            if (it) {
                                onIntent(DiscoverIntent.SelectCategory(category))
                            }
                        }
                    )
                )
            }
        }

        if (papers.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(papers, key = { it.id }) { paper ->
                    PaperCard(paper = paper)
                }
            }
        }
    }
}

@Composable
private fun PaperCard(paper: Paper) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = paper.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = paper.authors.joinToString(", "),
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = paper.abstract,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Blue, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = paper.field,
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "❤️ ${paper.likes}",
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No papers found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try selecting a different category",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray
            )
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
                        title = "Quantum Entanglement: The EPR Paradox Revisited",
                        authors = listOf("Alice Smith", "Bob Johnson"),
                        field = "Physics",
                        abstract = "This paper re-examines the famous EPR paradox and its implications for quantum mechanics.",
                        likes = 234
                    )
                ),
                selectedCategory = "Physics"
            ),
            onIntent = {}
        )
    }
}
