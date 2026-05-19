package com.bilibili.client.ui.navigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Video : Route("video/{bvid}") {
        fun createRoute(bvid: String) = "video/$bvid"
    }
    data object Live : Route("live/{roomId}") {
        fun createRoute(roomId: Long) = "live/$roomId"
    }
    data object Search : Route("search")
    data object Creator : Route("creator/{mid}") {
        fun createRoute(mid: Long) = "creator/$mid"
    }
    data object Downloads : Route("downloads")
    data object Settings : Route("settings")
    data object Login : Route("login")
}
