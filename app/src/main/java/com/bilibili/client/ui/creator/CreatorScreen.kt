package com.bilibili.client.ui.creator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bilibili.client.ui.components.EmptyState
import com.bilibili.client.ui.components.ErrorView
import com.bilibili.client.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorScreen(
    mid: Long,
    onNavigateToVideo: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreatorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mid) {
        viewModel.loadCreator(mid)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.userName.take(20)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorView(
                message = uiState.error!!,
                onRetry = { viewModel.loadCreator(mid) }
            )
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Avatar
                        if (uiState.avatar.isNotEmpty()) {
                            AsyncImage(
                                model = uiState.avatar,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                        } else {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("UP", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiState.userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "粉丝: ${formatCount(uiState.followerCount)}  |  视频: ${uiState.videoCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider()
                    }

                    // Videos
                    if (uiState.videos.isEmpty()) {
                        item {
                            EmptyState(message = "暂无视频")
                        }
                    } else {
                        items(uiState.videos) { video ->
                            CreatorVideoItem(
                                video = video,
                                onClick = { onNavigateToVideo(video.bvid) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorVideoItem(
    video: com.bilibili.client.domain.model.Video,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (video.coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = video.coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .width(120.dp)
                        .height(68.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .width(120.dp)
                        .height(68.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(video.duration, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatCount(video.views)} 次观看",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatCount(count: Long): String = when {
    count >= 10000 -> "${count / 10000}万"
    count >= 1000 -> "${count / 1000}千"
    else -> count.toString()
}
