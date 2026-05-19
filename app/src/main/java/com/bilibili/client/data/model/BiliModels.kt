package com.bilibili.client.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null
)

@Serializable
data class PopularResultDto(
    val list: List<VideoSummaryDto> = emptyList()
)

@Serializable
data class VideoSummaryDto(
    val bvid: String = "",
    val title: String = "",
    val pic: String = "",
    @SerialName("owner") val owner: OwnerDto? = null,
    @SerialName("stat") val stat: StatDto? = null,
    val duration: Long = 0
)

@Serializable
data class OwnerDto(
    val mid: Long = 0,
    val name: String = ""
)

@Serializable
data class StatDto(
    val view: Long = 0,
    val like: Long = 0,
    val coin: Long = 0,
    val favorite: Long = 0
)

@Serializable
data class VideoDetailDto(
    val bvid: String = "",
    val title: String = "",
    val desc: String = "",
    val pic: String = "",
    @SerialName("owner") val owner: OwnerDto? = null,
    @SerialName("stat") val stat: StatDto? = null,
    val cid: Long = 0,
    val duration: Long = 0,
    val pages: List<VideoPageDto> = emptyList()
)

@Serializable
data class VideoPageDto(
    val cid: Long = 0,
    val page: Int = 0,
    val part: String = ""
)

@Serializable
data class PlayUrlDto(
    val quality: Int = 0,
    val format: String = "",
    val timelength: Long = 0,
    @SerialName("accept_format") val acceptFormat: String = "",
    @SerialName("accept_quality") val acceptQuality: List<Int> = emptyList(),
    @SerialName("dash") val dash: DashDto? = null,
    @SerialName("durl") val durl: List<DurlDto>? = null
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
    @SerialName("author") val author: String = "",
    val duration: String = "",
    val play: Long = 0
)

@Serializable
data class SearchSuggestDto(
    val suggest: List<String> = emptyList()
)

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

@Serializable
data class LiveRoomDto(
    @SerialName("room_id") val roomId: Long = 0,
    val title: String = "",
    @SerialName("user_cover") val userCover: String = "",
    @SerialName("live_status") val liveStatus: Int = 0,
    @SerialName("online") val online: Long = 0,
    @SerialName("uname") val uname: String = ""
)
