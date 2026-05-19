package com.bilibili.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bilibili.client.core.theme.BilibiliTheme
import com.bilibili.client.ui.navigation.BilibiliNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BilibiliTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BilibiliNavHost()
                }
            }
        }
    }
}
