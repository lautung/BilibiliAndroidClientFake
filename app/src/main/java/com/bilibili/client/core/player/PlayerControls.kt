package com.bilibili.client.core.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun PlayerControls(
    playerState: PlayerState,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Update slider position while playing
    LaunchedEffect(playerState.isPlaying, playerState.currentPosition) {
        if (!isDragging) {
            sliderPosition = if (playerState.duration > 0) {
                (playerState.currentPosition.toFloat() / playerState.duration.toFloat()) * 100f
            } else 0f
        }
    }

    // Throttled position update during drag
    LaunchedEffect(isDragging) {
        if (isDragging) {
            while (isDragging) {
                delay(50)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Progress slider
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it; isDragging = true },
            onValueChangeFinished = {
                isDragging = false
                val targetMs = ((sliderPosition / 100f) * playerState.duration).toLong()
                onSeek(targetMs)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(playerState.currentPosition),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = formatTime(playerState.duration),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPlayPause) {
                Text(
                    text = if (playerState.isPlaying) "⏸" else "▶",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
