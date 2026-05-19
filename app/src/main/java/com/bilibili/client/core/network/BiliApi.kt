package com.bilibili.client.core.network

import com.bilibili.client.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BiliApi {

    // Video
    @GET("x/web-interface/popular")
    suspend fun getPopular(
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 20
    ): Response<BiliResponse<PopularResultDto>>

    @GET("x/web-interface/popular/series/one")
    suspend fun getRecommendedVideos(): Response<BiliResponse<PopularResultDto>>

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
    ): ResponseBody

    // Comments
    @GET("x/v2/medialist/comment/list")
    suspend fun getComments(
        @Query("aid") aid: Long,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 20
    ): Response<BiliResponse<CommentDataDto>>

    // Search
    @GET("x/web-interface/search/type")
    suspend fun searchVideos(
        @Query("keyword") keyword: String,
        @Query("search_type") searchType: String = "video",
        @Query("page") page: Int = 1
    ): Response<BiliResponse<SearchResultDto>>

    @GET("x/web-interface/search/default/suggest")
    suspend fun getSearchSuggestions(
        @Query("term") term: String
    ): Response<BiliResponse<SearchSuggestDto>>

    // Auth
    @GET("x/passport-login/web/qrcode/generate")
    suspend fun getLoginQrCode(): Response<BiliResponse<QrCodeDto>>

    @GET("x/passport-login/web/qrcode/poll")
    suspend fun pollQrLogin(
        @Query("qrcode_key") qrcodeKey: String
    ): Response<BiliResponse<QrPollDto>>

    @GET("x/web-interface/nav")
    suspend fun getCurrentUser(): Response<BiliResponse<NavDto>>

    @POST("x/passport-login/web/logout")
    suspend fun logout(): Response<BiliResponse<Unit>>

    // User
    @GET("x/space/wbi/arc/search")
    suspend fun getCreatorVideos(
        @Query("mid") mid: Long,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 30
    ): Response<BiliResponse<CreatorVideoDto>>

    @GET("x/space/acc/info")
    suspend fun getUserInfo(
        @Query("mid") mid: Long
    ): Response<BiliResponse<OwnerDto>>

    // Live
    @GET("room/v1/Room/get_info")
    suspend fun getLiveRoomInfo(
        @Query("room_id") roomId: Long
    ): Response<BiliResponse<LiveRoomDto>>

    @GET("room/v1/Room/get_status_info_by_uids")
    suspend fun getFollowedRooms(
        @Query("uids") uids: String
    ): Response<BiliResponse<Map<String, LiveRoomDto>>>

    @GET("room/v3/area/getRoomList")
    suspend fun getLiveRooms(
        @Query("area_id") areaId: Int = 0,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 30
    ): Response<BiliResponse<LiveRoomListDto>>

    @GET("room/v1/Room/playUrl")
    suspend fun getLivePlayUrl(
        @Query("room_id") roomId: Long,
        @Query("platform") platform: String = "web",
        @Query("protocol") protocol: String = "http_hls"
    ): Response<BiliResponse<LivePlayUrlDto>>
}
