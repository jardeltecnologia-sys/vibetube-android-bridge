package br.com.vibetube.app.data.blogger.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BloggerFeedParserTest {

    private val parser = BloggerFeedParser()

    @Test
    fun `extrai blogId de feed id`() {
        val id = "tag:blogger.com,1999:blog-1234567890"
        assertEquals("1234567890", parser.extractBlogId(id))
    }

    @Test
    fun `blogId null para input invalido`() {
        assertNull(parser.extractBlogId(null))
        assertNull(parser.extractBlogId(""))
        assertNull(parser.extractBlogId("foo bar"))
    }

    @Test
    fun `extrai postId de entry id`() {
        val id = "tag:blogger.com,1999:blog-1234567890.post-9876543210"
        assertEquals("9876543210", parser.extractPostId(id))
    }

    @Test
    fun `postId null para input sem padrao`() {
        assertNull(parser.extractPostId(null))
        assertNull(parser.extractPostId("tag:blogger.com,1999:blog-1234"))
    }

    @Test
    fun `extractPostUrl pega rel alternate html`() {
        val links = listOf(
            BloggerLink(rel = "self", type = "application/atom+xml", href = "https://feed.url"),
            BloggerLink(rel = "alternate", type = "text/html", href = "https://www.vibetube.com.br/2024/01/post.html")
        )
        assertEquals("https://www.vibetube.com.br/2024/01/post.html", parser.extractPostUrl(links))
    }

    @Test
    fun `extractPostUrl fallback se sem type html`() {
        val links = listOf(
            BloggerLink(rel = "alternate", type = null, href = "https://x.com/post")
        )
        assertEquals("https://x.com/post", parser.extractPostUrl(links))
    }

    @Test
    fun `extractPostUrl null se nenhum alternate`() {
        val links = listOf(BloggerLink(rel = "self", href = "x"))
        assertNull(parser.extractPostUrl(links))
    }

    @Test
    fun `buildCommentsAnchorUrl adiciona comments`() {
        val url = parser.buildCommentsAnchorUrl("https://www.vibetube.com.br/2024/01/post.html")
        assertEquals("https://www.vibetube.com.br/2024/01/post.html#comments", url)
    }

    @Test
    fun `buildCommentsAnchorUrl preserva anchor existente`() {
        val url = parser.buildCommentsAnchorUrl("https://x.com/post#secao")
        assertEquals("https://x.com/post#secao", url)
    }

    @Test
    fun `buildCommentsAnchorUrl null para entrada vazia`() {
        assertNull(parser.buildCommentsAnchorUrl(null))
        assertNull(parser.buildCommentsAnchorUrl(""))
    }

    @Test
    fun `parse JSON minimo do feed`() {
        val raw = """
            {
              "feed": {
                "id": {"${'$'}t": "tag:blogger.com,1999:blog-111"},
                "entry": [
                  {
                    "id": {"${'$'}t": "tag:blogger.com,1999:blog-111.post-222"},
                    "title": {"${'$'}t": "Titulo"},
                    "summary": {"${'$'}t": "Resumo"},
                    "published": {"${'$'}t": "2024-01-01T10:00:00.000-03:00"},
                    "link": [
                      {"rel": "alternate", "type": "text/html", "href": "https://x.com/p.html"}
                    ]
                  }
                ]
              }
            }
        """.trimIndent()
        val feed = parser.parse(raw)
        assertEquals(1, feed?.entries?.size)
        assertEquals("Titulo", feed?.entries?.first()?.title?.t)
    }
}
