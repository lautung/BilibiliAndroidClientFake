package com.bilibili.client.ui.live

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bilibili.client.ui.components.ErrorView
import com.bilibili.client.ui.components.LoadingIndicator
import com.bilibili.client.ui.video.components.VideoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    roomId: Long,
    onNavigateBack: () -> Unit,
    viewModel: LiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(roomId) {
        viewModel.loadRoom(roomId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.roomTitle.take(20).ifEmpty { "直播" }) },
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
                onRetry = { viewModel.loadRoom(roomId) }
            )
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Live Player
                    if (uiState.isPlayerReady) {
                        VideoPlayer(
                            player = viewModel.biliPlayer,
                            danmakuEngine = viewModel.danmakuEngine,
                            showDanmaku = false
                        )
                    } else {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (uiState.isLive) "直播中" else "直播已结束",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    // Room info
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = uiState.uploader,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${uiState.viewerCount} 人观看",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = if (uiState.isLive) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = if (uiState.isLive) "LIVE" else "已结束",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = if (uiState.isLive) MaterialTheme.colorScheme.onError
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    HorizontalDivider()

                    // Live chat placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "实时聊天将在后续版本中支持",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
