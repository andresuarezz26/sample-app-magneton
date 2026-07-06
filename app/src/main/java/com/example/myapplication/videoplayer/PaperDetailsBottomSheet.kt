package com.example.myapplication.videoplayer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.VideoItem

@Composable
fun PaperDetailsBottomSheet(
    video: VideoItem,
    isExpanded: Boolean,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit,
    isLiked: Boolean,
    isBookmarked: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1A1A2E),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(16.dp)
            .then(
                if (isExpanded) {
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                } else {
                    Modifier
                }
            )
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        ) {}

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = video.paperTitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("paperTitlePlayer")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = video.paperAuthors,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("paperAuthors")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = video.fieldTag,
            fontSize = 11.sp,
            color = Color.Cyan,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .background(Color.Cyan.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = video.paperAbstract,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.85f),
            lineHeight = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("paperAbstract")
        )

        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Read original paper",
                fontSize = 13.sp,
                color = Color.Cyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.paperUrl))
                        context.startActivity(intent)
                    }
                    .padding(8.dp)
                    .testTag("readOriginalLink")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = if (isLiked) "❤️" else "🤍",
                    label = "Like",
                    onClick = onLike,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btnLike")
                )
                ActionButton(
                    icon = if (isBookmarked) "🔖" else "🔗",
                    label = "Bookmark",
                    onClick = onBookmark,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = "➡️",
                    label = "Share",
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
    }
}
