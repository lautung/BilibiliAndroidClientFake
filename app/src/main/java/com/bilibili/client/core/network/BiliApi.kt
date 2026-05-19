package com.bilibili.client.core.network

import com.bilibili.client.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BiliApi {

    // Video
    @GET("x/web-interface/popular")
    suspend fun getPopular(): Response<BiliResponse<PopularResultDto>>

    @GET("x/web-interface/view")
    suspend fun getVideoDetail(@Query("bvid") bvid: String): Response<BiliResponse<VideoDetailDto>>

    @GET("x/player/playurl")
    suspend fun getPlayUrl(
        @Query("bvid") bvid: String,
        @Query("cid") cid: Long,
        @Query("qn") qn: Int = 80,
        @Query("fnver") fnver: Int = 0,
        @Query("fnval") fnval: Int = 4048,
        @Query("fourk") fourk: Int = 1
    ): Response<BiliResponse<PlayUrlDto>>

    // Danmaku
    @GET("x/v2/dm/web/seg.so")
    suspend fun getDanmaku(
        @Query("oid") oid: Long,
        @Query("type") type: Int = 1,
        @Query("segment") segment: Int = 1
    ): okhttp3.ResponseBody

    // Search
    @GET("x/web-interface/search/type")
    suspend fun search(
        @Query("keyword") keyword: String,
        @Query("search_type") searchType: String = "video",
        @Query("page") page: Int = 1
    ): Response<BiliResponse<SearchResultDto>>

    @GET("x/web-interface/search/default/suggest")
    suspend fun getSearchSuggest(
        @Query("term") term: String
    ): Response<BiliResponse<SearchSuggestDto>>

    // Auth
    @GET("x/passport-login/web/qrcode/generate")
    suspend fun generateQRCode(): Response<BiliResponse<QrCodeDto>>

    @GET("x/passport-login/web/qrcode/poll")
    suspend fun pollQRCode(
        @Query("qrcode_key") qrcodeKey: String
    ): Response<BiliResponse<QrPollDto>>

    // User
    @GET("x/space/wbi/arc/search")
    suspend fun getCreatorVideos(
        @Query("mid") mid: Long,
        @Query("ps") pageSize: Int = 30,
        @Query("pn") pageNum: Int = 1
    ): Response<BiliResponse<CreatorVideoDto>>

    // Live
    @GET("room/v1/Room/get_info")
    suspend fun getLiveRoomInfo(
        @Query("room_id") roomId: Long
    ): Response<BiliResponse<LiveRoomDto>>

    @GET("room/v1/Room/get_status_info_by_uids")
    suspend fun getFollowedRooms(
        @Query("uids") uids: String
    ): Response<BiliResponse<Map<String, LiveRoomDto>>>
}
