package br.com.vibetube.app.data.blogger.parser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTOs para parsing do feed JSON do Blogger.
 * O formato é "JSON-Atom" com campos como `$t` para texto.
 */

@Serializable
data class BloggerFeedRoot(
    @SerialName("feed") val feed: BloggerFeed? = null
)

@Serializable
data class BloggerFeed(
    @SerialName("id") val id: BloggerText? = null,
    @SerialName("updated") val updated: BloggerText? = null,
    @SerialName("title") val title: BloggerText? = null,
    @SerialName("subtitle") val subtitle: BloggerText? = null,
    @SerialName("entry") val entries: List<BloggerEntry> = emptyList()
)

@Serializable
data class BloggerEntry(
    @SerialName("id") val id: BloggerText? = null,
    @SerialName("published") val published: BloggerText? = null,
    @SerialName("updated") val updated: BloggerText? = null,
    @SerialName("title") val title: BloggerText? = null,
    @SerialName("summary") val summary: BloggerText? = null,
    @SerialName("content") val content: BloggerText? = null,
    @SerialName("link") val links: List<BloggerLink> = emptyList(),
    @SerialName("author") val authors: List<BloggerAuthor> = emptyList(),
    @SerialName("category") val categories: List<BloggerCategory> = emptyList(),
    @SerialName("media\$thumbnail") val thumbnail: BloggerThumbnail? = null,
    @SerialName("thr\$total") val total: BloggerText? = null
)

@Serializable
data class BloggerText(
    @SerialName("\$t") val t: String? = null,
    @SerialName("type") val type: String? = null
)

@Serializable
data class BloggerLink(
    @SerialName("rel") val rel: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("href") val href: String? = null,
    @SerialName("title") val title: String? = null
)

@Serializable
data class BloggerAuthor(
    @SerialName("name") val name: BloggerText? = null,
    @SerialName("uri") val uri: BloggerText? = null,
    @SerialName("email") val email: BloggerText? = null
)

@Serializable
data class BloggerCategory(
    @SerialName("scheme") val scheme: String? = null,
    @SerialName("term") val term: String? = null
)

@Serializable
data class BloggerThumbnail(
    @SerialName("url") val url: String? = null,
    @SerialName("height") val height: String? = null,
    @SerialName("width") val width: String? = null
)
