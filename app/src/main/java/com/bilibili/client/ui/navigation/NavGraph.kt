package com.bilibili.client.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bilibili.client.ui.creator.CreatorScreen
import com.bilibili.client.ui.download.DownloadScreen
import com.bilibili.client.ui.home.HomeScreen
import com.bilibili.client.ui.live.LiveScreen
import com.bilibili.client.ui.login.LoginScreen
import com.bilibili.client.ui.search.SearchScreen
import com.bilibili.client.ui.settings.SettingsScreen
import com.bilibili.client.ui.video.VideoScreen

@Composable
fun BilibiliNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        composable(Route.Home.route) {
            HomeScreen(
                onNavigateToVideo = { bvid -> navController.navigate(Route.Video.createRoute(bvid)) },
                onNavigateToLive = { roomId -> navController.navigate(Route.Live.createRoute(roomId)) },
                onNavigateToSearch = { navController.navigate(Route.Search.route) },
                onNavigateToDownloads = { navController.navigate(Route.Downloads.route) },
                onNavigateToSettings = { navController.navigate(Route.Settings.route) },
                onNavigateToLogin = { navController.navigate(Route.Login.route) }
            )
        }

        composable(
            route = Route.Video.route,
            arguments = listOf(navArgument("bvid") { type = NavType.StringType })
        ) { backStackEntry ->
            val bvid = backStackEntry.arguments?.getString("bvid") ?: return@composable
            VideoScreen(
                bvid = bvid,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreator = { mid -> navController.navigate(Route.Creator.createRoute(mid)) }
            )
        }

        composable(
            route = Route.Live.route,
            arguments = listOf(navArgument("roomId") { type = NavType.LongType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getLong("roomId") ?: return@composable
            LiveScreen(
                roomId = roomId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.Search.route) {
            SearchScreen(
                onNavigateToVideo = { bvid -> navController.navigate(Route.Video.createRoute(bvid)) },
                onNavigateToCreator = { mid -> navController.navigate(Route.Creator.createRoute(mid)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.Creator.route,
            arguments = listOf(navArgument("mid") { type = NavType.LongType })
        ) { backStackEntry ->
            val mid = backStackEntry.arguments?.getLong("mid") ?: return@composable
            CreatorScreen(
                mid = mid,
                onNavigateToVideo = { bvid -> navController.navigate(Route.Video.createRoute(bvid)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.Downloads.route) {
            DownloadScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Route.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Route.Login.route) {
            LoginScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
