package com.zagavideodown.app.models


import androidx.annotation.Keep
import com.zagavideodown.app.models.bulkdownloader.DashInfo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

val mapper = jacksonObjectMapper().apply {
    propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class InstagramPostDataNew(
    @get:JsonProperty("__typename") @field:JsonProperty("__typename")
    val typename: String? = null,

    @get:JsonProperty("__isXDTGraphMediaInterface") @field:JsonProperty("__isXDTGraphMediaInterface")
    val isXDTGraphMediaInterface: String? = null,

    val id: String? = null,
    val shortcode: String? = null,

    @get:JsonProperty("thumbnail_src") @field:JsonProperty("thumbnail_src")
    val thumbnailSrc: String? = null,

    val dimensions: Dimensions? = null,

    @get:JsonProperty("gating_info") @field:JsonProperty("gating_info")
    val gatingInfo: Any? = null,

    @get:JsonProperty("fact_check_overall_rating") @field:JsonProperty("fact_check_overall_rating")
    val factCheckOverallRating: Any? = null,

    @get:JsonProperty("fact_check_information") @field:JsonProperty("fact_check_information")
    val factCheckInformation: Any? = null,

    @get:JsonProperty("sensitivity_friction_info") @field:JsonProperty("sensitivity_friction_info")
    val sensitivityFrictionInfo: Any? = null,

    @get:JsonProperty("sharing_friction_info") @field:JsonProperty("sharing_friction_info")
    val sharingFrictionInfo: SharingFrictionInfo? = null,

    @get:JsonProperty("media_overlay_info") @field:JsonProperty("media_overlay_info")
    val mediaOverlayInfo: Any? = null,

    @get:JsonProperty("media_preview") @field:JsonProperty("media_preview")
    val mediaPreview: Any? = null,

    @get:JsonProperty("display_url") @field:JsonProperty("display_url")
    val displayURL: String? = null,

    @get:JsonProperty("display_resources") @field:JsonProperty("display_resources")
    val displayResources: List<DisplayResource>? = null,

    @get:JsonProperty("is_video") @field:JsonProperty("is_video")
    val isVideo: Boolean? = null,

    @get:JsonProperty("tracking_token") @field:JsonProperty("tracking_token")
    val trackingToken: String? = null,

    @get:JsonProperty("upcoming_event") @field:JsonProperty("upcoming_event")
    val upcomingEvent: Any? = null,

    @get:JsonProperty("edge_media_to_tagged_user") @field:JsonProperty("edge_media_to_tagged_user")
    val edgeMediaToTaggedUser: EdgeMediaToTaggedUser? = null,

    val owner: InstagramPostDataNewOwner? = null,

    @get:JsonProperty("accessibility_caption") @field:JsonProperty("accessibility_caption")
    val accessibilityCaption: String? = null,

    @get:JsonProperty("edge_sidecar_to_children") @field:JsonProperty("edge_sidecar_to_children")
    val edgeSidecarToChildren: EdgeSidecarToChildren? = null,

    @get:JsonProperty("edge_media_to_caption") @field:JsonProperty("edge_media_to_caption")
    val edgeMediaToCaption: EdgeMediaToCaptionClass? = null,

    @get:JsonProperty("can_see_insights_as_brand") @field:JsonProperty("can_see_insights_as_brand")
    val canSeeInsightsAsBrand: Boolean? = null,

    @get:JsonProperty("caption_is_edited") @field:JsonProperty("caption_is_edited")
    val captionIsEdited: Boolean? = null,

    @get:JsonProperty("has_ranked_comments") @field:JsonProperty("has_ranked_comments")
    val hasRankedComments: Boolean? = null,

    @get:JsonProperty("like_and_view_counts_disabled") @field:JsonProperty("like_and_view_counts_disabled")
    val likeAndViewCountsDisabled: Boolean? = null,

    @get:JsonProperty("edge_media_to_parent_comment") @field:JsonProperty("edge_media_to_parent_comment")
    val edgeMediaToParentComment: EdgeMediaToParentCommentClass? = null,

    @get:JsonProperty("edge_media_to_hoisted_comment") @field:JsonProperty("edge_media_to_hoisted_comment")
    val edgeMediaToHoistedComment: EdgeMediaToCaptionClass? = null,

    @get:JsonProperty("edge_media_preview_comment") @field:JsonProperty("edge_media_preview_comment")
    val edgeMediaPreviewComment: EdgeMediaPreview? = null,

    @get:JsonProperty("comments_disabled") @field:JsonProperty("comments_disabled")
    val commentsDisabled: Boolean? = null,

    @get:JsonProperty("commenting_disabled_for_viewer") @field:JsonProperty("commenting_disabled_for_viewer")
    val commentingDisabledForViewer: Boolean? = null,

    @get:JsonProperty("taken_at_timestamp") @field:JsonProperty("taken_at_timestamp")
    val takenAtTimestamp: Long? = null,

    @get:JsonProperty("edge_media_preview_like") @field:JsonProperty("edge_media_preview_like")
    val edgeMediaPreviewLike: EdgeMediaPreview? = null,

    @get:JsonProperty("edge_media_to_sponsor_user") @field:JsonProperty("edge_media_to_sponsor_user")
    val edgeMediaToSponsorUser: EdgeMediaToCaptionClass? = null,

    @get:JsonProperty("is_affiliate") @field:JsonProperty("is_affiliate")
    val isAffiliate: Boolean? = null,

    @get:JsonProperty("is_paid_partnership") @field:JsonProperty("is_paid_partnership")
    val isPaidPartnership: Boolean? = null,

    val location: Location? = null,

    @get:JsonProperty("nft_asset_info") @field:JsonProperty("nft_asset_info")
    val nftAssetInfo: Any? = null,

    @get:JsonProperty("viewer_has_liked") @field:JsonProperty("viewer_has_liked")
    val viewerHasLiked: Boolean? = null,

    @get:JsonProperty("viewer_has_saved") @field:JsonProperty("viewer_has_saved")
    val viewerHasSaved: Boolean? = null,

    @get:JsonProperty("viewer_has_saved_to_collection") @field:JsonProperty("viewer_has_saved_to_collection")
    val viewerHasSavedToCollection: Boolean? = null,

    @get:JsonProperty("viewer_in_photo_of_you") @field:JsonProperty("viewer_in_photo_of_you")
    val viewerInPhotoOfYou: Boolean? = null,

    @get:JsonProperty("viewer_can_reshare") @field:JsonProperty("viewer_can_reshare")
    val viewerCanReshare: Boolean? = null,

    @get:JsonProperty("is_ad") @field:JsonProperty("is_ad")
    val isAd: Boolean? = null,

    @get:JsonProperty("edge_web_media_to_related_media") @field:JsonProperty("edge_web_media_to_related_media")
    val edgeWebMediaToRelatedMedia: EdgeMediaToCaptionClass? = null,

    @get:JsonProperty("coauthor_producers") @field:JsonProperty("coauthor_producers")
    val coauthorProducers: List<Any?>? = null,

    @get:JsonProperty("pinned_for_users") @field:JsonProperty("pinned_for_users")
    val pinnedForUsers: List<Any?>? = null,

    @get:JsonProperty("dash_info") @field:JsonProperty("dash_info")
    val dashInfo: DashInfo? = null,

    @get:JsonProperty("has_audio") @field:JsonProperty("has_audio")
    val hasAudio: Boolean? = null,

    @get:JsonProperty("video_url") @field:JsonProperty("video_url")
    val videoURL: String? = null,

    @get:JsonProperty("video_view_count") @field:JsonProperty("video_view_count")
    val videoViewCount: Long? = null,

    @get:JsonProperty("video_play_count") @field:JsonProperty("video_play_count")
    val videoPlayCount: Long? = null,

    @get:JsonProperty("encoding_status") @field:JsonProperty("encoding_status")
    val encodingStatus: Any? = null,

    @get:JsonProperty("is_published") @field:JsonProperty("is_published")
    val isPublished: Boolean? = null,
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        fun fromJson(json: String) = mapper.readValue<InstagramPostDataNew>(json)
    }
}

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class Dimensions(
    val height: Long? = null,
    val width: Long? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class DisplayResource(
    val src: String? = null,

    @get:JsonProperty("config_width") @field:JsonProperty("config_width")
    val configWidth: Long? = null,

    @get:JsonProperty("config_height") @field:JsonProperty("config_height")
    val configHeight: Long? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeMediaPreview(
    val count: Long? = null,
    val edges: List<EdgeMediaPreviewCommentEdge>? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeMediaToParentCommentClass(
    val count: Long? = null,

    @get:JsonProperty("page_info") @field:JsonProperty("page_info")
    val pageInfo: PageInfo? = null,

    val edges: List<EdgeMediaPreviewCommentEdge>? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class PurpleNode(
    val id: String? = null,
    val text: String? = null,

    @get:JsonProperty("created_at") @field:JsonProperty("created_at")
    val createdAt: Long? = null,

    @get:JsonProperty("did_report_as_spam") @field:JsonProperty("did_report_as_spam")
    val didReportAsSpam: Boolean? = null,

    val owner: NodeOwner? = null,

    @get:JsonProperty("viewer_has_liked") @field:JsonProperty("viewer_has_liked")
    val viewerHasLiked: Boolean? = null,

    @get:JsonProperty("edge_liked_by") @field:JsonProperty("edge_liked_by")
    val edgeLikedBy: EdgeFollowedByClass? = null,

    @get:JsonProperty("is_restricted_pending") @field:JsonProperty("is_restricted_pending")
    val isRestrictedPending: Boolean? = null,

    @get:JsonProperty("edge_threaded_comments") @field:JsonProperty("edge_threaded_comments")
    val edgeThreadedComments: EdgeMediaToParentCommentClass? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeMediaPreviewCommentEdge(
    val node: PurpleNode? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class PageInfo(
    @get:JsonProperty("has_next_page") @field:JsonProperty("has_next_page")
    val hasNextPage: Boolean? = null,

    @get:JsonProperty("end_cursor") @field:JsonProperty("end_cursor")
    val endCursor: String? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeFollowedByClass(
    val count: Long? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class NodeOwner(
    val id: String? = null,

    @get:JsonProperty("is_verified") @field:JsonProperty("is_verified")
    val isVerified: Boolean? = null,

    @get:JsonProperty("profile_pic_url") @field:JsonProperty("profile_pic_url")
    val profilePicURL: String? = null,

    val username: String? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeMediaToCaptionClass(
    val edges: List<EdgeMediaToCaptionEdge>? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeMediaToCaptionEdge(
    val node: FluffyNode? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class FluffyNode(
    @get:JsonProperty("created_at") @field:JsonProperty("created_at")
    val createdAt: String? = null,

    val text: String? = null,
    val id: String? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeMediaToTaggedUser(
    val edges: List<EdgeMediaToTaggedUserEdge>? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeMediaToTaggedUserEdge(
    val node: TentacledNode? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class TentacledNode(
    val user: User? = null,
    val x: Double? = null,
    val y: Double? = null,
    val id: String? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    @get:JsonProperty("full_name") @field:JsonProperty("full_name")
    val fullName: String? = null,

    @get:JsonProperty("followed_by_viewer") @field:JsonProperty("followed_by_viewer")
    val followedByViewer: Boolean? = null,

    val id: String? = null,

    @get:JsonProperty("is_verified") @field:JsonProperty("is_verified")
    val isVerified: Boolean? = null,

    @get:JsonProperty("profile_pic_url") @field:JsonProperty("profile_pic_url")
    val profilePicURL: String? = null,

    val username: String? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeSidecarToChildren(
    val edges: List<EdgeSidecarToChildrenEdge>? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class EdgeSidecarToChildrenEdge(
    val node: StickyNode? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class StickyNode(
    @get:JsonProperty("__typename") @field:JsonProperty("__typename")
    val typename: String? = null,

    val id: String? = null,
    val shortcode: String? = null,
    val dimensions: Dimensions? = null,

    @get:JsonProperty("gating_info") @field:JsonProperty("gating_info")
    val gatingInfo: Any? = null,

    @get:JsonProperty("fact_check_overall_rating") @field:JsonProperty("fact_check_overall_rating")
    val factCheckOverallRating: Any? = null,

    @get:JsonProperty("fact_check_information") @field:JsonProperty("fact_check_information")
    val factCheckInformation: Any? = null,

    @get:JsonProperty("sensitivity_friction_info") @field:JsonProperty("sensitivity_friction_info")
    val sensitivityFrictionInfo: Any? = null,

    @get:JsonProperty("sharing_friction_info") @field:JsonProperty("sharing_friction_info")
    val sharingFrictionInfo: SharingFrictionInfo? = null,

    @get:JsonProperty("media_overlay_info") @field:JsonProperty("media_overlay_info")
    val mediaOverlayInfo: Any? = null,

    @get:JsonProperty("media_preview") @field:JsonProperty("media_preview")
    val mediaPreview: String? = null,

    @get:JsonProperty("display_url") @field:JsonProperty("display_url")
    val displayURL: String? = null,

    @get:JsonProperty("display_resources") @field:JsonProperty("display_resources")
    val displayResources: List<DisplayResource>? = null,

    @get:JsonProperty("accessibility_caption") @field:JsonProperty("accessibility_caption")
    val accessibilityCaption: String? = null,

    @get:JsonProperty("is_video") @field:JsonProperty("is_video")
    val isVideo: Boolean? = null,

    @get:JsonProperty("tracking_token") @field:JsonProperty("tracking_token")
    val trackingToken: String? = null,

    @get:JsonProperty("upcoming_event") @field:JsonProperty("upcoming_event")
    val upcomingEvent: Any? = null,

    @get:JsonProperty("edge_media_to_tagged_user") @field:JsonProperty("edge_media_to_tagged_user")
    val edgeMediaToTaggedUser: EdgeMediaToTaggedUser? = null,

    @get:JsonProperty("dash_info") @field:JsonProperty("dash_info")
    val dashInfo: DashInfo? = null,

    @get:JsonProperty("has_audio") @field:JsonProperty("has_audio")
    val hasAudio: Boolean? = null,

    @get:JsonProperty("video_url") @field:JsonProperty("video_url")
    val videoURL: String? = null,

    @get:JsonProperty("video_view_count") @field:JsonProperty("video_view_count")
    val videoViewCount: Long? = null,

    @get:JsonProperty("video_play_count") @field:JsonProperty("video_play_count")
    val videoPlayCount: Long? = null,

    @get:JsonProperty("encoding_status") @field:JsonProperty("encoding_status")
    val encodingStatus: Any? = null,

    @get:JsonProperty("is_published") @field:JsonProperty("is_published")
    val isPublished: Boolean? = null,
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class SharingFrictionInfo(
    @get:JsonProperty("should_have_sharing_friction") @field:JsonProperty("should_have_sharing_friction")
    val shouldHaveSharingFriction: Boolean? = null,

    @get:JsonProperty("bloks_app_url") @field:JsonProperty("bloks_app_url")
    val bloksAppURL: Any? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(
    val id: String? = null,

    @get:JsonProperty("has_public_page") @field:JsonProperty("has_public_page")
    val hasPublicPage: Boolean? = null,

    val name: String? = null,
    val slug: String? = null,

    @get:JsonProperty("address_json") @field:JsonProperty("address_json")
    val addressJSON: String? = null
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class InstagramPostDataNewOwner(
    val id: String? = null,
    val username: String? = null,

    @get:JsonProperty("is_verified") @field:JsonProperty("is_verified")
    val isVerified: Boolean? = null,

    @get:JsonProperty("profile_pic_url") @field:JsonProperty("profile_pic_url")
    val profilePicURL: String? = null,

    @get:JsonProperty("blocked_by_viewer") @field:JsonProperty("blocked_by_viewer")
    val blockedByViewer: Boolean? = null,

    @get:JsonProperty("restricted_by_viewer") @field:JsonProperty("restricted_by_viewer")
    val restrictedByViewer: Any? = null,

    @get:JsonProperty("followed_by_viewer") @field:JsonProperty("followed_by_viewer")
    val followedByViewer: Boolean? = null,

    @get:JsonProperty("full_name") @field:JsonProperty("full_name")
    val fullName: String? = null,

    @get:JsonProperty("has_blocked_viewer") @field:JsonProperty("has_blocked_viewer")
    val hasBlockedViewer: Boolean? = null,

    @get:JsonProperty("is_embeds_disabled") @field:JsonProperty("is_embeds_disabled")
    val isEmbedsDisabled: Boolean? = null,

    @get:JsonProperty("is_private") @field:JsonProperty("is_private")
    val isPrivate: Boolean? = null,

    @get:JsonProperty("is_unpublished") @field:JsonProperty("is_unpublished")
    val isUnpublished: Boolean? = null,

    @get:JsonProperty("requested_by_viewer") @field:JsonProperty("requested_by_viewer")
    val requestedByViewer: Boolean? = null,

    @get:JsonProperty("pass_tiering_recommendation") @field:JsonProperty("pass_tiering_recommendation")
    val passTieringRecommendation: Boolean? = null,

    @get:JsonProperty("edge_owner_to_timeline_media") @field:JsonProperty("edge_owner_to_timeline_media")
    val edgeOwnerToTimelineMedia: EdgeFollowedByClass? = null,

    @get:JsonProperty("edge_followed_by") @field:JsonProperty("edge_followed_by")
    val edgeFollowedBy: EdgeFollowedByClass? = null,


    )

