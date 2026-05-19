package com.bilibili.client.ui.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bilibili.client.ui.components.EmptyState
import com.bilibili.client.ui.components.ErrorView
import com.bilibili.client.ui.components.LoadingIndicator
import com.bilibili.client.ui.video.components.VideoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    bvid: String,
    onNavigateBack: () -> Unit,
    onNavigateToCreator: (Long) -> Unit,
    viewModel: VideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Allow viewModel to load video when bvid changes
    LaunchedEffect(bvid) {
        if (uiState.videoInfo?.bvid != bvid) {
            viewModel.loadVideo(bvid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.videoInfo?.title?.take(30) ?: "视频") },
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
                onRetry = { viewModel.loadVideo(bvid) }
            )
            uiState.videoInfo != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Video Player with Danmaku
                    item {
                        VideoPlayer(
                            player = viewModel.biliPlayer,
                            danmakuEngine = viewModel.danmakuEngine,
                            playerPositionMs = uiState.playerPositionMs,
                            showDanmaku = true
                        )
                    }

                    // Player controls
                    item {
                        PlayerActionBar(
                            isPlaying = viewModel.biliPlayer.state.value.isPlaying,
                            onPlayPause = { viewModel.togglePlayPause() }
                        )
                    }

                    // Video Info
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = uiState.videoInfo!!.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = { onNavigateToCreator(uiState.videoInfo!!.uploaderMid) }) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(uiState.videoInfo!!.uploader)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Stats row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ActionButton(Icons.Default.ThumbUp, uiState.videoInfo!!.likes)
                                ActionButton(Icons.Default.ThumbDown, "踩")
                                ActionButton(Icons.Default.Star, uiState.videoInfo!!.favorites)
                                ActionButton(Icons.Default.Share, "分享")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = uiState.videoInfo!!.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Related videos
                    if (uiState.relatedVideos.isNotEmpty()) {
                        item {
                            HorizontalDivider()
                            Text(
                                text = "相关推荐",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        items(uiState.relatedVideos.take(5)) { video ->
                            RelatedVideoItem(video)
                        }
                    }

                    // Comments section
                    item {
                        HorizontalDivider()
                        Text(
                            text = "评论",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    if (uiState.comments.isEmpty()) {
                        item {
                            EmptyState(
                                message = "暂无评论",
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    } else {
                        items(uiState.comments) { comment ->
                            CommentItem(comment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerActionBar(
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPlayPause) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "暂停" else "播放",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { }) {
            Icon(icon, contentDescription = text)
        }
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun CommentItem(comment: CommentItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.username,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun RelatedVideoItem(video: com.bilibili.client.ui.home.VideoItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Surface(
            modifier = Modifier
                .width(120.dp)
                .height(68.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = video.duration,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                text = video.uploader,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${video.views} 次观看",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
