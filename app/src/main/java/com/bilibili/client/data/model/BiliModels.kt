package com.bilibili.client.data.model

import com.bilibili.client.domain.model.DanmakuItem
import com.bilibili.client.domain.model.DanmakuType
import com.bilibili.client.domain.model.Video
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val ttl: Int = 0,
    val data: T? = null
)

// region Popular / Feed

@Serializable
data class PopularResultDto(
    val list: List<VideoSummaryDto> = emptyList()
)

@Serializable
data class VideoSummaryDto(
    val bvid: String = "",
    val title: String = "",
    val pic: String = "",
    val owner: OwnerDto? = null,
    val stat: StatDto? = null,
    val duration: Long = 0
)

@Serializable
data class OwnerDto(
    val mid: Long = 0,
    val name: String = "",
    val face: String = ""
)

@Serializable
data class StatDto(
    val view: Long = 0,
    val like: Long = 0,
    val coin: Long = 0,
    val favorite: Long = 0,
    val danmaku: Long = 0
)

// endregion

// region Video Detail

@Serializable
data class VideoDetailDto(
    val bvid: String = "",
    val aid: Long = 0,
    val title: String = "",
    val desc: String = "",
    val pic: String = "",
    val owner: OwnerDto? = null,
    val stat: StatDto? = null,
    val cid: Long = 0,
    val duration: Long = 0,
    val pages: List<VideoPageDto> = emptyList(),
    val related: List<VideoSummaryDto>? = null,
    val tname: String = ""
)

@Serializable
data class VideoPageDto(
    val cid: Long = 0,
    val page: Int = 0,
    val part: String = ""
)

// endregion

// region Play URL

@Serializable
data class PlayUrlDto(
    val quality: Int = 0,
    val format: String = "",
    val timelength: Long = 0,
    @SerialName("accept_format") val acceptFormat: String = "",
    @SerialName("accept_quality") val acceptQuality: List<Int> = emptyList(),
    @SerialName("accept_description") val acceptDescription: List<String>? = null,
    val dash: DashDto? = null,
    val durl: List<DurlDto>? = null
)

@Serializable
data class DashDto(
    val duration: Long = 0,
    val video: List<StreamDto> = emptyList(),
    val audio: List<StreamDto> = emptyList()
)

@Serializable
data class StreamDto(
    val id: Int = 0,
    @SerialName("base_url") val baseUrl: String = "",
    @SerialName("backup_url") val backupUrl: List<String> = emptyList(),
    val bandwidth: Long = 0,
    val codecid: Int = 0,
    @SerialName("mime_type") val mimeType: String = "",
    @SerialName("codecs") val codecs: String = ""
)

@Serializable
data class DurlDto(
    val url: String = "",
    @SerialName("backup_url") val backupUrl: List<String> = emptyList(),
    val size: Long = 0,
    val length: Long = 0
)

// endregion

// region Search

@Serializable
data class SearchResultDto(
    val numResults: Long = 0,
    val result: List<SearchVideoDto> = emptyList()
)

@Serializable
data class SearchVideoDto(
    val bvid: String = "",
    val title: String = "",
    val pic: String = "",
    val author: String = "",
    val duration: String = "",
    val play: Long = 0
)

@Serializable
data class SearchSuggestDto(
    val suggest: List<String> = emptyList()
)

// endregion

// region Comments

@Serializable
data class CommentDataDto(
    val page: CommentPageDto? = null,
    val replies: List<CommentReplyDto>? = null
)

@Serializable
data class CommentPageDto(
    val count: Int = 0,
    val num: Int = 1,
    val size: Int = 20
)

@Serializable
data class CommentReplyDto(
    val rpid: Long = 0,
    val oid: Long = 0,
    val type: Int = 1,
    val mid: Long = 0,
    val ctime: Long = 0,
    val like: Int = 0,
    val replies: Int = 0,
    val member: CommentMemberDto? = null,
    val content: CommentContentDto? = null
)

@Serializable
data class CommentMemberDto(
    val mid: String = "",
    val name: String = "",
    val avatar: String = ""
)

@Serializable
data class CommentContentDto(
    val message: String = ""
)

// endregion

// region Auth

@Serializable
data class QrCodeDto(
    val url: String = "",
    @SerialName("qrcode_key") val qrcodeKey: String = ""
)

@Serializable
data class QrPollDto(
    val code: Int = 0,
    val message: String = "",
    val url: String = ""
)

@Serializable
data class NavDto(
    val mid: String = "",
    val name: String = "",
    val face: String = "",
    val sign: String = "",
    val levelInfo: LevelInfoDto? = null,
    @SerialName("is_login") val isLogin: Boolean = false
)

@Serializable
data class LevelInfoDto(
    @SerialName("current_level") val currentLevel: Int = 0
)

// endregion

// region Live

@Serializable
data class LiveRoomListDto(
    val list: List<LiveRoomDto> = emptyList()
)

@Serializable
data class LiveRoomDto(
    @SerialName("room_id") val roomId: Long = 0,
    val uid: Long = 0,
    val title: String = "",
    val uname: String = "",
    val face: String = "",
    @SerialName("user_cover") val userCover: String = "",
    val key: String? = null,
    @SerialName("live_status") val liveStatus: Int = 0,
    @SerialName("online") val online: Long = 0,
    @SerialName("play_url") val playUrl: String? = null,
    @SerialName("cover") val coverUrl: String? = null,
    @SerialName("is_live") val isLive: Int = 0
)

@Serializable
data class LivePlayUrlDto(
    val durl: List<LiveDurlDto>? = null,
    val protocol: String? = null
)

@Serializable
data class LiveDurlDto(
    val url: String = "",
    val host: String = ""
)

// endregion

// region Creator

@Serializable
data class CreatorVideoDto(
    val list: List<VideoSummaryDto> = emptyList(),
    val page: PageInfoDto? = null
)

@Serializable
data class PageInfoDto(
    val pn: Int = 1,
    val ps: Int = 30,
    val count: Int = 0
)

// endregion

// region Mapping Extensions

fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s)
    else "%d:%02d".format(m, s)
}

fun VideoSummaryDto.toDomainVideo(): Video = Video(
    bvid = bvid,
    aid = 0,
    title = title,
    description = "",
    uploader = owner?.name ?: "",
    uploaderAvatar = owner?.face ?: "",
    uploaderMid = owner?.mid ?: 0,
    views = stat?.view ?: 0,
    danmakuCount = stat?.danmaku ?: 0,
    likes = stat?.like ?: 0,
    coins = stat?.coin ?: 0,
    favorites = stat?.favorite ?: 0,
    duration = formatDuration(duration),
    coverUrl = pic,
    picUrl = pic,
    pubdate = 0,
    tname = "",
    quality = 0,
    acceptQuality = emptyList(),
    dashVideoUrl = null,
    dashAudioUrl = null
)

fun VideoDetailDto.toDomainVideo(): Video = Video(
    bvid = bvid,
    aid = aid,
    title = title,
    description = desc,
    uploader = owner?.name ?: "",
    uploaderAvatar = owner?.face ?: "",
    uploaderMid = owner?.mid ?: 0,
    views = stat?.view ?: 0,
    danmakuCount = stat?.danmaku ?: 0,
    likes = stat?.like ?: 0,
    coins = stat?.coin ?: 0,
    favorites = stat?.favorite ?: 0,
    duration = formatDuration(duration),
    coverUrl = pic,
    picUrl = pic,
    pubdate = 0,
    tname = tname,
    quality = 0,
    acceptQuality = emptyList(),
    relatedVideos = related?.map { it.toDomainVideo() } ?: emptyList()
)

// endregion
