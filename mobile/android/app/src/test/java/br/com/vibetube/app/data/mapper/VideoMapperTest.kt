package br.com.vibetube.app.data.mapper

import br.com.vibetube.app.data.blogger.parser.BloggerEntry
import br.com.vibetube.app.data.blogger.parser.BloggerFeed
import br.com.vibetube.app.data.blogger.parser.BloggerLink
import br.com.vibetube.app.data.blogger.parser.BloggerText
import br.com.vibetube.app.data.blogger.parser.BloggerThumbnail
import br.com.vibetube.app.data.blogger.parser.BloggerAuthor
import br.com.vibetube.app.data.blogger.parser.BloggerCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoMapperTest {

    private val mapper = VideoMapper()

    @Test
    fun `mapeia entry com YouTube embed para VibeVideo`() {
        val entry = BloggerEntry(
            id = BloggerText(t = "tag:blogger.com,1999:blog-111.post-222"),
            title = BloggerText(t = "Meu vídeo legal"),
            summary = BloggerText(t = "Um resumo aqui"),
            content = BloggerText(
                t = """<iframe src="https://www.youtube.com/embed/AbCdEfGhIjK"></iframe>"""
            ),
            published = BloggerText(t = "2024-05-01T10:00:00.000-03:00"),
            updated = BloggerText(t = "2024-05-02T10:00:00.000-03:00"),
            links = listOf(
                BloggerLink(rel = "alternate", type = "text/html",
                    href = "https://www.vibetube.com.br/2024/05/post.html")
            ),
            authors = listOf(BloggerAuthor(name = BloggerText(t = "Autor"))),
            categories = listOf(BloggerCategory(term = "tag1"), BloggerCategory(term = "tag2"))
        )

        val video = mapper.mapSingle(entry, blogIdHint = "111")
        assertNotNull(video)
        assertEquals("Meu vídeo legal", video!!.title)
        assertEquals("222", video.postId)
        assertEquals("111", video.blogId)
        assertEquals("https://www.vibetube.com.br/2024/05/post.html", video.postUrl)
        assertEquals("https://www.youtube.com/embed/AbCdEfGhIjK", video.embedUrl)
        assertNull(video.videoUrl)
        assertEquals("blogger", video.source)
        assertEquals(2, video.labels.size)
        assertTrue(video.canShare)
        assertTrue(video.canLike)
    }

    @Test
    fun `mapeia entry com mp4 direto`() {
        val entry = BloggerEntry(
            id = BloggerText(t = "tag:blogger.com,1999:blog-1.post-2"),
            title = BloggerText(t = "Direct video"),
            content = BloggerText(t = """<video src="https://x.com/vid.mp4"/>"""),
            links = listOf(
                BloggerLink(rel = "alternate", type = "text/html", href = "https://x.com/p.html")
            )
        )
        val video = mapper.mapSingle(entry, blogIdHint = null)
        assertNotNull(video)
        assertEquals("https://x.com/vid.mp4", video!!.videoUrl)
        assertNull(video.embedUrl)
    }

    @Test
    fun `mapper devolve null se sem postUrl`() {
        val entry = BloggerEntry(
            id = BloggerText(t = "tag:blogger.com,1999:blog-1.post-2"),
            title = BloggerText(t = "Sem url"),
            links = emptyList()
        )
        assertNull(mapper.mapSingle(entry, blogIdHint = null))
    }

    @Test
    fun `mapeia feed inteiro`() {
        val feed = BloggerFeed(
            id = BloggerText(t = "tag:blogger.com,1999:blog-9999"),
            entries = listOf(
                BloggerEntry(
                    id = BloggerText(t = "tag:blogger.com,1999:blog-9999.post-1"),
                    title = BloggerText(t = "A"),
                    links = listOf(BloggerLink(rel = "alternate", href = "https://x.com/a"))
                ),
                BloggerEntry(
                    id = BloggerText(t = "tag:blogger.com,1999:blog-9999.post-2"),
                    title = BloggerText(t = "B"),
                    links = listOf(BloggerLink(rel = "alternate", href = "https://x.com/b"))
                )
            )
        )
        val videos = mapper.mapEntries(feed)
        assertEquals(2, videos.size)
        assertTrue(videos.all { it.blogId == "9999" })
    }

    @Test
    fun `intro vem do summary se disponivel`() {
        val entry = BloggerEntry(
            id = BloggerText(t = "tag:blogger.com,1999:blog-1.post-2"),
            title = BloggerText(t = "T"),
            summary = BloggerText(t = "Texto curto resumo"),
            content = BloggerText(t = "<p>Conteúdo longo no body</p>"),
            links = listOf(BloggerLink(rel = "alternate", href = "https://x.com/p"))
        )
        val video = mapper.mapSingle(entry, null)
        assertEquals("Texto curto resumo", video?.intro)
    }

    @Test
    fun `commentsUrl construido com anchor`() {
        val entry = BloggerEntry(
            id = BloggerText(t = "tag:blogger.com,1999:blog-1.post-2"),
            title = BloggerText(t = "T"),
            links = listOf(BloggerLink(rel = "alternate", href = "https://x.com/p"))
        )
        val video = mapper.mapSingle(entry, null)
        assertEquals("https://x.com/p#comments", video?.commentsUrl)
    }
}
